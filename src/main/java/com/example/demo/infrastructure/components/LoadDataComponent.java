package com.example.demo.infrastructure.components;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.demo.domain.models.entities.Categoria;
import com.example.demo.domain.models.entities.Perfil;
import com.example.demo.domain.models.entities.Produto;
import com.example.demo.domain.models.entities.UnidadeMedida;
import com.example.demo.domain.models.entities.Usuario;
import com.example.demo.infrastructure.repositories.CategoriaRepository;
import com.example.demo.infrastructure.repositories.PerfilRepository;
import com.example.demo.infrastructure.repositories.ProdutoRepository;
import com.example.demo.infrastructure.repositories.UsuarioRepository;

@Component
public class LoadDataComponent implements ApplicationRunner {

	@Autowired
	private PerfilRepository perfilRepository;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private CategoriaRepository categoriaRepository;

	@Autowired
	private ProdutoRepository produtoRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Value("${app.admin.nome}")
	private String adminNome;

	@Value("${app.admin.sobrenome}")
	private String adminSobrenome;

	@Value("${app.admin.username}")
	private String adminUsername;

	@Value("${app.admin.email}")
	private String adminEmail;

	@Value("${app.admin.senha}")
	private String adminSenha;

	private static final UUID ID_ADMINISTRADOR = UUID.fromString("cfd306c0-c516-4176-a215-bb7a49e54c6f");
	private static final UUID ID_OPERADOR = UUID.fromString("7f55d810-f21a-4052-9d39-6ef61cbe85b2");

	private record ProdutoSeed(String nome, String preco, Integer quantidade, UnidadeMedida unidadeMedida) {
	}

	private record UsuarioSeed(String nome, String sobrenome, String username, String email, String senha,
			String perfil) {
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		seedPerfil(ID_ADMINISTRADOR, "Administrador");
		seedPerfil(ID_OPERADOR, "Operador");
		seedAdminUsuario();
		seedCategoriasEProdutos();
		seedUsuariosAdicionais();
	}

	private void seedPerfil(UUID id, String nome) {

		if (perfilRepository.existsById(id))
			return;

		var perfil = new Perfil();
		perfil.setId(id);
		perfil.setNome(nome);

		perfilRepository.save(perfil);
	}

	private void seedAdminUsuario() {

		if (usuarioRepository.findByUsername(adminUsername) != null)
			return;

		var usuario = new Usuario();
		usuario.setId(UUID.randomUUID());
		usuario.setNome(adminNome);
		usuario.setSobrenome(adminSobrenome);
		usuario.setUsername(adminUsername);
		usuario.setEmail(adminEmail);
		usuario.setSenha(passwordEncoder.encode(adminSenha));
		usuario.setPerfil(perfilRepository.findByNome("Administrador"));

		usuarioRepository.save(usuario);
	}

	private void seedCategoriasEProdutos() {

		for (var entry : catalogo().entrySet()) {

			Categoria categoria = seedCategoria(entry.getKey());

			for (ProdutoSeed produtoSeed : entry.getValue())
				seedProduto(produtoSeed, categoria);
		}
	}

	private Categoria seedCategoria(String nome) {

		Categoria existente = categoriaRepository.findByNome(nome);

		if (existente != null)
			return existente;

		var categoria = new Categoria();
		categoria.setId(UUID.randomUUID());
		categoria.setNome(nome);

		return categoriaRepository.save(categoria);
	}

	private void seedProduto(ProdutoSeed seed, Categoria categoria) {

		if (produtoRepository.existsByNome(seed.nome()))
			return;

		var produto = new Produto();
		produto.setId(UUID.randomUUID());
		produto.setNome(seed.nome());
		produto.setPreco(new BigDecimal(seed.preco()));
		produto.setQuantidade(seed.quantidade());
		produto.setUnidadeMedida(seed.unidadeMedida());
		produto.setCategoria(categoria);

		produtoRepository.save(produto);
	}

	private void seedUsuariosAdicionais() {

		for (UsuarioSeed seed : usuariosAdicionais()) {

			if (usuarioRepository.findByUsername(seed.username()) != null)
				continue;

			var usuario = new Usuario();
			usuario.setId(UUID.randomUUID());
			usuario.setNome(seed.nome());
			usuario.setSobrenome(seed.sobrenome());
			usuario.setUsername(seed.username());
			usuario.setEmail(seed.email());
			usuario.setSenha(passwordEncoder.encode(seed.senha()));
			usuario.setPerfil(perfilRepository.findByNome(seed.perfil()));

			usuarioRepository.save(usuario);
		}
	}

	private Map<String, List<ProdutoSeed>> catalogo() {

		Map<String, List<ProdutoSeed>> catalogo = new LinkedHashMap<>();

		catalogo.put("Hortifruti", List.of(
				new ProdutoSeed("Banana Prata", "6.99", 50, UnidadeMedida.KG),
				new ProdutoSeed("Maçã Fuji", "8.49", 40, UnidadeMedida.KG),
				new ProdutoSeed("Tomate", "7.29", 35, UnidadeMedida.KG),
				new ProdutoSeed("Alface Crespa", "3.50", 30, UnidadeMedida.UNIDADE)));

		catalogo.put("Padaria", List.of(
				new ProdutoSeed("Pão Francês", "1.20", 100, UnidadeMedida.UNIDADE),
				new ProdutoSeed("Pão de Forma", "9.90", 25, UnidadeMedida.UNIDADE),
				new ProdutoSeed("Bolo de Chocolate", "24.90", 10, UnidadeMedida.UNIDADE)));

		catalogo.put("Laticínios e Frios", List.of(
				new ProdutoSeed("Leite Integral 1L", "5.49", 60, UnidadeMedida.UNIDADE),
				new ProdutoSeed("Queijo Mussarela", "42.90", 20, UnidadeMedida.KG),
				new ProdutoSeed("Iogurte Natural", "6.20", 30, UnidadeMedida.UNIDADE),
				new ProdutoSeed("Requeijão", "8.99", 25, UnidadeMedida.UNIDADE)));

		catalogo.put("Carnes e Aves", List.of(
				new ProdutoSeed("Peito de Frango", "16.90", 40, UnidadeMedida.KG),
				new ProdutoSeed("Carne Moída", "32.90", 30, UnidadeMedida.KG),
				new ProdutoSeed("Linguiça Toscana", "19.90", 25, UnidadeMedida.KG)));

		catalogo.put("Bebidas", List.of(
				new ProdutoSeed("Água Mineral 500ml", "2.50", 120, UnidadeMedida.UNIDADE),
				new ProdutoSeed("Refrigerante Cola 2L", "8.99", 50, UnidadeMedida.UNIDADE),
				new ProdutoSeed("Suco de Laranja 1L", "7.50", 40, UnidadeMedida.LITRO),
				new ProdutoSeed("Cerveja Pilsen 350ml", "4.20", 80, UnidadeMedida.UNIDADE)));

		catalogo.put("Mercearia", List.of(
				new ProdutoSeed("Arroz Branco 5kg", "24.90", 40, UnidadeMedida.UNIDADE),
				new ProdutoSeed("Feijão Carioca 1kg", "8.90", 35, UnidadeMedida.UNIDADE),
				new ProdutoSeed("Açúcar Refinado 1kg", "4.50", 50, UnidadeMedida.UNIDADE),
				new ProdutoSeed("Óleo de Soja 900ml", "7.99", 45, UnidadeMedida.LITRO),
				new ProdutoSeed("Café Torrado 500g", "14.90", 30, UnidadeMedida.UNIDADE)));

		catalogo.put("Limpeza", List.of(
				new ProdutoSeed("Detergente Neutro", "2.99", 70, UnidadeMedida.UNIDADE),
				new ProdutoSeed("Sabão em Pó 1kg", "12.90", 25, UnidadeMedida.UNIDADE),
				new ProdutoSeed("Água Sanitária 1L", "4.50", 40, UnidadeMedida.LITRO),
				new ProdutoSeed("Amaciante 2L", "15.90", 20, UnidadeMedida.LITRO)));

		catalogo.put("Higiene Pessoal", List.of(
				new ProdutoSeed("Sabonete", "2.50", 100, UnidadeMedida.UNIDADE),
				new ProdutoSeed("Shampoo 350ml", "16.90", 30, UnidadeMedida.UNIDADE),
				new ProdutoSeed("Pasta de Dente", "5.90", 45, UnidadeMedida.UNIDADE),
				new ProdutoSeed("Papel Higiênico 12 rolos", "21.90", 25, UnidadeMedida.CAIXA)));

		return catalogo;
	}

	private List<UsuarioSeed> usuariosAdicionais() {

		return List.of(
				new UsuarioSeed("João", "Silva", "joao.silva", "joao.silva@supermarket.com", "Operador@123",
						"Operador"),
				new UsuarioSeed("Maria", "Souza", "maria.souza", "maria.souza@supermarket.com", "Operador@123",
						"Operador"),
				new UsuarioSeed("Carlos", "Pereira", "carlos.pereira", "carlos.pereira@supermarket.com",
						"Admin@123", "Administrador"));
	}
}
