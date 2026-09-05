package com.example.demo.infrastructure.components;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.demo.domain.models.entities.Perfil;
import com.example.demo.domain.models.entities.Usuario;
import com.example.demo.infrastructure.repositories.PerfilRepository;
import com.example.demo.infrastructure.repositories.UsuarioRepository;

@Component
public class LoadDataComponent implements ApplicationRunner {

	@Autowired
	private PerfilRepository perfilRepository;

	@Autowired
	private UsuarioRepository usuarioRepository;

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

	@Override
	public void run(ApplicationArguments args) throws Exception {
		seedPerfil(ID_ADMINISTRADOR, "Administrador");
		seedPerfil(ID_OPERADOR, "Operador");
		seedAdminUsuario();
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
}
