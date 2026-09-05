package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.example.demo.application.dtos.AutenticarUsuarioRequestDto;
import com.example.demo.application.dtos.AutenticarUsuarioResponseDto;
import com.example.demo.application.dtos.CategoriaRequestDto;
import com.example.demo.application.dtos.CategoriaResponseDto;
import com.example.demo.application.dtos.CriarUsuarioRequestDto;
import com.example.demo.application.dtos.CriarUsuarioResponseDto;
import com.example.demo.application.dtos.ProdutoRequestDto;
import com.example.demo.application.dtos.ProdutoResponseDto;
import com.example.demo.application.dtos.RegistrarMovimentacaoEstoqueRequestDto;
import com.example.demo.domain.models.entities.TipoMovimentacao;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SupermarketApiApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private static UUID idCategoriaTeste;
	private static String nomeCategoriaTeste;
	private static UUID idProdutoTeste;
	private static String nomeProdutoTeste;
	private static String emailUsuarioTeste;
	private static String usernameUsuarioTeste;
	private static String senhaUsuarioTeste;
	private static String tokenAdminTeste;
	private static String tokenUsuarioTeste;

	@Test
	@Order(1)
	public void autenticarAdminTest() throws Exception {

		var request = new AutenticarUsuarioRequestDto();
		request.setUsername("admin");
		request.setSenha("Admin@123");

		MvcResult result = mockMvc.perform(post("/api/usuario/autenticar").contentType("application/json")
				.content(objectMapper.writeValueAsString(request))).andExpect(status().isOk()).andReturn();

		var content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);

		var response = objectMapper.readValue(content, AutenticarUsuarioResponseDto.class);

		assertNotNull(response.getToken());
		assertEquals("Administrador", response.getPerfil());

		tokenAdminTeste = response.getToken();
	}

	@Test
	@Order(2)
	public void criarUsuarioTest() throws Exception {

		var request = new CriarUsuarioRequestDto();
		var faker = new Faker();

		request.setNome(faker.name().firstName());
		request.setSobrenome(faker.name().lastName());
		request.setUsername(faker.name().username());
		request.setEmail(faker.internet().emailAddress());
		request.setSenha("Senha@123");
		request.setPerfil("Operador");

		MvcResult result = mockMvc.perform(post("/api/usuario/criar").contentType("application/json")
				.header("Authorization", "Bearer " + tokenAdminTeste)
				.content(objectMapper.writeValueAsString(request))).andExpect(status().isOk()).andReturn();

		var content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);

		var response = objectMapper.readValue(content, CriarUsuarioResponseDto.class);

		assertNotNull(response.getId());
		assertEquals(request.getNome(), response.getNome());
		assertEquals(request.getSobrenome(), response.getSobrenome());
		assertEquals(request.getUsername(), response.getUsername());
		assertEquals(request.getEmail(), response.getEmail());
		assertEquals("Operador", response.getPerfil());

		emailUsuarioTeste = request.getEmail();
		usernameUsuarioTeste = request.getUsername();
		senhaUsuarioTeste = request.getSenha();
	}

	@Test
	@Order(3)
	public void usuarioComEmailDuplicadoTest() throws Exception {

		var request = new CriarUsuarioRequestDto();
		var faker = new Faker();

		request.setNome("Outro");
		request.setSobrenome("Usuario Teste");
		request.setUsername(faker.name().username());
		request.setEmail(emailUsuarioTeste);
		request.setSenha("Senha@123");
		request.setPerfil("Operador");

		MvcResult result = mockMvc
				.perform(post("/api/usuario/criar").contentType("application/json")
						.header("Authorization", "Bearer " + tokenAdminTeste)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest()).andReturn();

		String content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);

		assertEquals("O e-mail informado já está cadastrado, tente outro.", content);
	}

	@Test
	@Order(4)
	public void credenciaisInvalidasTest() throws Exception {

		var request = new AutenticarUsuarioRequestDto();
		request.setUsername(usernameUsuarioTeste);
		request.setSenha("SenhaErrada@123");

		MvcResult result = mockMvc
				.perform(post("/api/usuario/autenticar").contentType("application/json")
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest()).andReturn();

		String content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);

		assertEquals("Acesso negado. Usuário não encontrado.", content);
	}

	@Test
	@Order(5)
	public void autenticarUsuarioTest() throws Exception {

		var request = new AutenticarUsuarioRequestDto();
		request.setUsername(usernameUsuarioTeste);
		request.setSenha(senhaUsuarioTeste);

		MvcResult result = mockMvc.perform(post("/api/usuario/autenticar").contentType("application/json")
				.content(objectMapper.writeValueAsString(request))).andExpect(status().isOk()).andReturn();

		var content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);

		var response = objectMapper.readValue(content, AutenticarUsuarioResponseDto.class);

		assertNotNull(response.getToken());
		assertEquals(usernameUsuarioTeste, response.getUsername());

		tokenUsuarioTeste = response.getToken();
	}

	@Test
	@Order(6)
	public void criarCategoriaTest() throws Exception {

		var request = new CategoriaRequestDto();
		var faker = new Faker();

		request.setNome(faker.commerce().department());

		MvcResult result = mockMvc.perform(post("/api/categorias").contentType("application/json")
				.header("Authorization", "Bearer " + tokenUsuarioTeste)
				.content(objectMapper.writeValueAsString(request))).andExpect(status().isOk()).andReturn();

		var content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);

		var response = objectMapper.readValue(content, CategoriaResponseDto.class);

		assertNotNull(response.getId());
		assertEquals(request.getNome(), response.getNome());

		idCategoriaTeste = response.getId();
		nomeCategoriaTeste = request.getNome();
	}

	@Test
	@Order(7)
	public void categoriaComNomeDuplicadoTest() throws Exception {

		var request = new CategoriaRequestDto();
		request.setNome(nomeCategoriaTeste);

		MvcResult result = mockMvc
				.perform(post("/api/categorias").contentType("application/json")
						.header("Authorization", "Bearer " + tokenUsuarioTeste)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest()).andReturn();

		String content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);

		assertEquals("Já existe uma categoria cadastrada com o nome: " + request.getNome() + ".", content);
	}

	@Test
	@Order(8)
	public void criarProdutoTest() throws Exception {

		var request = new ProdutoRequestDto();
		var faker = new Faker();

		request.setNome(faker.commerce().productName());
		double precoDouble = faker.number().randomDouble(2, 1, 1000);
		request.setPreco(BigDecimal.valueOf(precoDouble));
		request.setQuantidade(10);
		request.setCategoriaId(idCategoriaTeste);

		MvcResult result = mockMvc.perform(
				post("/api/produtos").contentType("application/json")
						.header("Authorization", "Bearer " + tokenUsuarioTeste)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk()).andReturn();

		var content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);

		var response = objectMapper.readValue(content, ProdutoResponseDto.class);

		assertNotNull(response.getId());
		assertEquals(request.getNome(), response.getNome());
		assertEquals(request.getPreco(), response.getPreco());
		assertEquals(request.getQuantidade(), response.getQuantidade());
		assertEquals(request.getCategoriaId(), response.getCategoria().getId());

		idProdutoTeste = response.getId();
		nomeProdutoTeste = request.getNome();
	}

	@Test
	@Order(9)
	public void registrarMovimentacaoEntradaTest() throws Exception {

		var request = new RegistrarMovimentacaoEstoqueRequestDto();
		request.setProdutoId(idProdutoTeste);
		request.setTipo(TipoMovimentacao.ENTRADA);
		request.setQuantidade(5);
		request.setMotivo("Reposição de fornecedor");

		MvcResult result = mockMvc
				.perform(post("/api/movimentacoes-estoque").contentType("application/json")
						.header("Authorization", "Bearer " + tokenUsuarioTeste)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk()).andReturn();

		var content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
		var response = objectMapper.readTree(content);

		assertEquals("ENTRADA", response.get("tipo").asText());
		assertEquals(5, response.get("quantidade").asInt());
		assertEquals(idProdutoTeste.toString(), response.get("produtoId").asText());

		MvcResult produtoResult = mockMvc
				.perform(get("/api/produtos/" + idProdutoTeste).header("Authorization", "Bearer " + tokenUsuarioTeste))
				.andExpect(status().isOk()).andReturn();

		var produto = objectMapper.readValue(produtoResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
				ProdutoResponseDto.class);

		assertEquals(15, produto.getQuantidade());
	}

	@Test
	@Order(10)
	public void registrarMovimentacaoSaidaTest() throws Exception {

		var request = new RegistrarMovimentacaoEstoqueRequestDto();
		request.setProdutoId(idProdutoTeste);
		request.setTipo(TipoMovimentacao.SAIDA);
		request.setQuantidade(3);
		request.setMotivo("Venda no caixa");

		mockMvc.perform(post("/api/movimentacoes-estoque").contentType("application/json")
				.header("Authorization", "Bearer " + tokenUsuarioTeste)
				.content(objectMapper.writeValueAsString(request))).andExpect(status().isOk());

		MvcResult produtoResult = mockMvc
				.perform(get("/api/produtos/" + idProdutoTeste).header("Authorization", "Bearer " + tokenUsuarioTeste))
				.andExpect(status().isOk()).andReturn();

		var produto = objectMapper.readValue(produtoResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
				ProdutoResponseDto.class);

		assertEquals(12, produto.getQuantidade());
	}

	@Test
	@Order(11)
	public void movimentacaoComEstoqueInsuficienteTest() throws Exception {

		var request = new RegistrarMovimentacaoEstoqueRequestDto();
		request.setProdutoId(idProdutoTeste);
		request.setTipo(TipoMovimentacao.SAIDA);
		request.setQuantidade(999);
		request.setMotivo("Tentativa de saída maior que o estoque");

		MvcResult result = mockMvc
				.perform(post("/api/movimentacoes-estoque").contentType("application/json")
						.header("Authorization", "Bearer " + tokenUsuarioTeste)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest()).andReturn();

		String content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);

		assertEquals("Estoque insuficiente para o produto '" + nomeProdutoTeste + "': disponível 12, solicitado 999.",
				content);
	}

	@Test
	@Order(12)
	public void consultarHistoricoMovimentacaoTest() throws Exception {

		MvcResult result = mockMvc
				.perform(get("/api/movimentacoes-estoque/produto/" + idProdutoTeste)
						.header("Authorization", "Bearer " + tokenUsuarioTeste))
				.andExpect(status().isOk()).andReturn();

		var content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
		var response = objectMapper.readTree(content);

		assertEquals(2, response.get("totalElementos").asInt());
		assertEquals("SAIDA", response.get("conteudo").get(0).get("tipo").asText());
		assertEquals("ENTRADA", response.get("conteudo").get(1).get("tipo").asText());
	}

	@Test
	@Order(13)
	public void produtoComNomeDuplicadoTest() throws Exception {

		var request = new ProdutoRequestDto();
		request.setNome(nomeProdutoTeste);
		request.setPreco(BigDecimal.valueOf(10));
		request.setQuantidade(10);
		request.setCategoriaId(idCategoriaTeste);

		MvcResult result = mockMvc
				.perform(post("/api/produtos").contentType("application/json")
						.header("Authorization", "Bearer " + tokenUsuarioTeste)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest()).andReturn();

		String content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);

		assertEquals("Já existe um produto cadastrado com o nome: " + request.getNome() + ".", content);
	}

	@Test
	@Order(14)
	public void produtoComEstoqueTest() throws Exception {

		MvcResult result = mockMvc.perform(delete("/api/produtos/" + idProdutoTeste).contentType("application/json")
				.header("Authorization", "Bearer " + tokenUsuarioTeste))
				.andExpect(status().isBadRequest()).andReturn();

		String content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);

		assertEquals("Não é possível excluir o produto '" + nomeProdutoTeste + "' porque ainda possui " + 12
				+ " unidades em estoque.", content);
	}

	@Test
	@Order(15)
	public void editarProdutoTest() throws Exception {

		var request = new ProdutoRequestDto();
		var faker = new Faker();

		request.setNome(faker.commerce().productName());
		double precoDouble = faker.number().randomDouble(2, 1, 1000);
		request.setPreco(BigDecimal.valueOf(precoDouble));
		request.setQuantidade(0);
		request.setCategoriaId(idCategoriaTeste);

		MvcResult result = mockMvc.perform(put("/api/produtos/" + idProdutoTeste).contentType("application/json")
				.header("Authorization", "Bearer " + tokenUsuarioTeste)
				.content(objectMapper.writeValueAsString(request))).andExpect(status().isOk()).andReturn();

		nomeProdutoTeste = request.getNome();

		var content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);

		var response = objectMapper.readValue(content, ProdutoResponseDto.class);

		assertNotNull(response.getId());
		assertEquals(request.getNome(), response.getNome());
		assertEquals(request.getPreco(), response.getPreco());
		assertEquals(request.getQuantidade(), response.getQuantidade());
		assertEquals(request.getCategoriaId(), response.getCategoria().getId());
	}

	@Test
	@Order(16)
	public void excluirProdutoTest() throws Exception {

		MvcResult result = mockMvc.perform(delete("/api/produtos/" + idProdutoTeste).contentType("application/json")
				.header("Authorization", "Bearer " + tokenUsuarioTeste))
				.andExpect(status().isOk()).andReturn();

		String content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);

		assertEquals("Produto \"" + nomeProdutoTeste + "\" excluído com sucesso!", content);
	}

	@Test
	@Order(17)
	public void excluirCategoriaTest() throws Exception {

		MvcResult result = mockMvc
				.perform(delete("/api/categorias/" + idCategoriaTeste).contentType("application/json")
						.header("Authorization", "Bearer " + tokenUsuarioTeste))
				.andExpect(status().isOk()).andReturn();

		String content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);

		assertEquals("Categoria \"" + nomeCategoriaTeste + "\" excluída com sucesso!", content);
	}

}
