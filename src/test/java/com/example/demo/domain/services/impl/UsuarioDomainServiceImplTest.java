package com.example.demo.domain.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.application.dtos.AutenticarUsuarioRequestDto;
import com.example.demo.application.dtos.CriarUsuarioRequestDto;
import com.example.demo.domain.exceptions.CredenciaisInvalidasException;
import com.example.demo.domain.exceptions.UsuarioComEmailDuplicadoException;
import com.example.demo.domain.exceptions.UsuarioComUsernameDuplicadoException;
import com.example.demo.domain.models.entities.Perfil;
import com.example.demo.domain.models.entities.Usuario;
import com.example.demo.infrastructure.components.JwtTokenComponent;
import com.example.demo.infrastructure.components.SHA256Component;
import com.example.demo.infrastructure.repositories.PerfilRepository;
import com.example.demo.infrastructure.repositories.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class UsuarioDomainServiceImplTest {

	@Mock
	private UsuarioRepository usuarioRepository;

	@Mock
	private PerfilRepository perfilRepository;

	@Mock
	private SHA256Component sha256Component;

	@Mock
	private JwtTokenComponent jwtTokenComponent;

	@InjectMocks
	private UsuarioDomainServiceImpl usuarioDomainService;

	private CriarUsuarioRequestDto criarRequest;
	private Perfil perfil;

	@BeforeEach
	void setUp() {
		criarRequest = new CriarUsuarioRequestDto();
		criarRequest.setNome("João");
		criarRequest.setSobrenome("Silva");
		criarRequest.setUsername("joao.silva");
		criarRequest.setEmail("joao@teste.com");
		criarRequest.setSenha("Senha@123");
		criarRequest.setPerfil("Operador");

		perfil = new Perfil();
		perfil.setId(UUID.randomUUID());
		perfil.setNome("Operador");
	}

	@Test
	void criarUsuario_deveLancarExcecao_quandoEmailJaCadastrado() {

		when(usuarioRepository.findByEmail("joao@teste.com")).thenReturn(new Usuario());

		assertThrows(UsuarioComEmailDuplicadoException.class,
				() -> usuarioDomainService.criarUsuario(criarRequest));

		verify(usuarioRepository, never()).save(any());
	}

	@Test
	void criarUsuario_deveLancarExcecao_quandoUsernameJaCadastrado() {

		when(usuarioRepository.findByEmail("joao@teste.com")).thenReturn(null);
		when(usuarioRepository.findByUsername("joao.silva")).thenReturn(new Usuario());

		assertThrows(UsuarioComUsernameDuplicadoException.class,
				() -> usuarioDomainService.criarUsuario(criarRequest));

		verify(usuarioRepository, never()).save(any());
	}

	@Test
	void criarUsuario_deveLancarExcecao_quandoPerfilInvalido() {

		when(usuarioRepository.findByEmail("joao@teste.com")).thenReturn(null);
		when(usuarioRepository.findByUsername("joao.silva")).thenReturn(null);
		when(perfilRepository.findByNome("Operador")).thenReturn(null);

		assertThrows(IllegalArgumentException.class, () -> usuarioDomainService.criarUsuario(criarRequest));

		verify(usuarioRepository, never()).save(any());
	}

	@Test
	void criarUsuario_deveCriar_quandoValido() {

		when(usuarioRepository.findByEmail("joao@teste.com")).thenReturn(null);
		when(usuarioRepository.findByUsername("joao.silva")).thenReturn(null);
		when(perfilRepository.findByNome("Operador")).thenReturn(perfil);
		when(sha256Component.encrypt("Senha@123")).thenReturn("hash-da-senha");

		var response = usuarioDomainService.criarUsuario(criarRequest);

		assertNotNull(response.getId());
		assertEquals("João", response.getNome());
		assertEquals("Silva", response.getSobrenome());
		assertEquals("joao.silva", response.getUsername());
		assertEquals("joao@teste.com", response.getEmail());
		assertEquals("Operador", response.getPerfil());
		assertNotNull(response.getDataCriacao());
		verify(usuarioRepository, times(1)).save(any(Usuario.class));
	}

	@Test
	void autenticarUsuario_deveLancarExcecao_quandoCredenciaisInvalidas() {

		var request = new AutenticarUsuarioRequestDto();
		request.setUsername("joao.silva");
		request.setSenha("senhaErrada1");

		when(sha256Component.encrypt("senhaErrada1")).thenReturn("hash-errado");
		when(usuarioRepository.findByUsernameAndSenha("joao.silva", "hash-errado")).thenReturn(null);

		assertThrows(CredenciaisInvalidasException.class,
				() -> usuarioDomainService.autenticarUsuario(request));
	}

	@Test
	void autenticarUsuario_deveAutenticar_quandoCredenciaisValidas() {

		var request = new AutenticarUsuarioRequestDto();
		request.setUsername("joao.silva");
		request.setSenha("Senha@123");

		var usuario = new Usuario();
		usuario.setId(UUID.randomUUID());
		usuario.setNome("João");
		usuario.setUsername("joao.silva");
		usuario.setEmail("joao@teste.com");
		usuario.setPerfil(perfil);

		when(sha256Component.encrypt("Senha@123")).thenReturn("hash-da-senha");
		when(usuarioRepository.findByUsernameAndSenha("joao.silva", "hash-da-senha")).thenReturn(usuario);
		when(jwtTokenComponent.getToken(usuario)).thenReturn("token-jwt");

		var response = usuarioDomainService.autenticarUsuario(request);

		assertEquals("joao.silva", response.getUsername());
		assertEquals("Operador", response.getPerfil());
		assertEquals("token-jwt", response.getToken());
	}
}
