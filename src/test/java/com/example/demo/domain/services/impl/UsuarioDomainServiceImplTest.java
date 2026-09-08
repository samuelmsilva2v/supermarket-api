package com.example.demo.domain.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.application.dtos.AtualizarStatusUsuarioRequestDto;
import com.example.demo.application.dtos.AutenticarUsuarioRequestDto;
import com.example.demo.application.dtos.CriarUsuarioRequestDto;
import com.example.demo.application.dtos.EditarUsuarioRequestDto;
import com.example.demo.application.dtos.UsuarioFiltroRequestDto;
import com.example.demo.domain.exceptions.CredenciaisInvalidasException;
import com.example.demo.domain.exceptions.UsuarioComEmailDuplicadoException;
import com.example.demo.domain.exceptions.UsuarioComUsernameDuplicadoException;
import com.example.demo.domain.exceptions.UsuarioInativoException;
import com.example.demo.domain.models.entities.Perfil;
import com.example.demo.domain.models.entities.Usuario;
import com.example.demo.infrastructure.components.JwtTokenComponent;
import com.example.demo.infrastructure.repositories.PerfilRepository;
import com.example.demo.infrastructure.repositories.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class UsuarioDomainServiceImplTest {

	@Mock
	private UsuarioRepository usuarioRepository;

	@Mock
	private PerfilRepository perfilRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

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
		when(passwordEncoder.encode("Senha@123")).thenReturn("hash-da-senha");

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
	void autenticarUsuario_deveLancarExcecao_quandoUsuarioNaoExiste() {

		var request = new AutenticarUsuarioRequestDto();
		request.setUsername("joao.silva");
		request.setSenha("senhaErrada1");

		when(usuarioRepository.findByUsername("joao.silva")).thenReturn(null);

		assertThrows(CredenciaisInvalidasException.class,
				() -> usuarioDomainService.autenticarUsuario(request));
	}

	@Test
	void autenticarUsuario_deveLancarExcecao_quandoSenhaInvalida() {

		var request = new AutenticarUsuarioRequestDto();
		request.setUsername("joao.silva");
		request.setSenha("senhaErrada1");

		var usuario = new Usuario();
		usuario.setUsername("joao.silva");
		usuario.setSenha("hash-da-senha");
		usuario.setPerfil(perfil);

		when(usuarioRepository.findByUsername("joao.silva")).thenReturn(usuario);
		when(passwordEncoder.matches("senhaErrada1", "hash-da-senha")).thenReturn(false);

		assertThrows(CredenciaisInvalidasException.class,
				() -> usuarioDomainService.autenticarUsuario(request));
	}

	@Test
	void autenticarUsuario_deveLancarExcecao_quandoUsuarioInativo() {

		var request = new AutenticarUsuarioRequestDto();
		request.setUsername("joao.silva");
		request.setSenha("Senha@123");

		var usuario = new Usuario();
		usuario.setUsername("joao.silva");
		usuario.setSenha("hash-da-senha");
		usuario.setPerfil(perfil);
		usuario.setAtivo(false);

		when(usuarioRepository.findByUsername("joao.silva")).thenReturn(usuario);
		when(passwordEncoder.matches("Senha@123", "hash-da-senha")).thenReturn(true);

		assertThrows(UsuarioInativoException.class, () -> usuarioDomainService.autenticarUsuario(request));
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
		usuario.setSenha("hash-da-senha");
		usuario.setPerfil(perfil);

		when(usuarioRepository.findByUsername("joao.silva")).thenReturn(usuario);
		when(passwordEncoder.matches("Senha@123", "hash-da-senha")).thenReturn(true);
		when(jwtTokenComponent.getToken(usuario)).thenReturn("token-jwt");

		var response = usuarioDomainService.autenticarUsuario(request);

		assertEquals("joao.silva", response.getUsername());
		assertEquals("Operador", response.getPerfil());
		assertEquals("token-jwt", response.getToken());
	}

	@Test
	void consultarUsuariosPaginado_deveRetornarPaginaMapeada() {

		var usuario = new Usuario();
		usuario.setId(UUID.randomUUID());
		usuario.setNome("João");
		usuario.setSobrenome("Silva");
		usuario.setUsername("joao.silva");
		usuario.setEmail("joao@teste.com");
		usuario.setPerfil(perfil);

		var page = new PageImpl<>(List.of(usuario), PageRequest.of(0, 10), 1);
		var filtro = new UsuarioFiltroRequestDto();
		filtro.setUsername("joao");

		when(usuarioRepository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(page);

		var response = usuarioDomainService.consultarUsuariosPaginado(filtro, 0, 10);

		assertEquals(1, response.getConteudo().size());
		assertEquals("joao.silva", response.getConteudo().get(0).getUsername());
		assertEquals("Operador", response.getConteudo().get(0).getPerfil());
		assertEquals(1, response.getTotalElementos());
	}

	@Test
	void editarUsuario_deveLancarExcecao_quandoUsuarioNaoEncontrado() {

		var id = UUID.randomUUID();
		var request = new EditarUsuarioRequestDto();

		when(usuarioRepository.findById(id)).thenReturn(Optional.empty());

		assertThrows(EntityNotFoundException.class, () -> usuarioDomainService.editarUsuario(id, request));

		verify(usuarioRepository, never()).save(any());
	}

	@Test
	void editarUsuario_deveLancarExcecao_quandoEmailJaCadastradoParaOutroUsuario() {

		var id = UUID.randomUUID();
		var usuario = new Usuario();
		usuario.setId(id);

		var request = new EditarUsuarioRequestDto();
		request.setEmail("joao@teste.com");
		request.setUsername("joao.silva");

		when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
		when(usuarioRepository.existsByEmailAndIdNot("joao@teste.com", id)).thenReturn(true);

		assertThrows(UsuarioComEmailDuplicadoException.class,
				() -> usuarioDomainService.editarUsuario(id, request));

		verify(usuarioRepository, never()).save(any());
	}

	@Test
	void editarUsuario_deveLancarExcecao_quandoUsernameJaCadastradoParaOutroUsuario() {

		var id = UUID.randomUUID();
		var usuario = new Usuario();
		usuario.setId(id);

		var request = new EditarUsuarioRequestDto();
		request.setEmail("joao@teste.com");
		request.setUsername("joao.silva");

		when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
		when(usuarioRepository.existsByEmailAndIdNot("joao@teste.com", id)).thenReturn(false);
		when(usuarioRepository.existsByUsernameAndIdNot("joao.silva", id)).thenReturn(true);

		assertThrows(UsuarioComUsernameDuplicadoException.class,
				() -> usuarioDomainService.editarUsuario(id, request));

		verify(usuarioRepository, never()).save(any());
	}

	@Test
	void editarUsuario_deveLancarExcecao_quandoPerfilInvalido() {

		var id = UUID.randomUUID();
		var usuario = new Usuario();
		usuario.setId(id);

		var request = new EditarUsuarioRequestDto();
		request.setEmail("joao@teste.com");
		request.setUsername("joao.silva");
		request.setPerfil("Operador");

		when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
		when(usuarioRepository.existsByEmailAndIdNot("joao@teste.com", id)).thenReturn(false);
		when(usuarioRepository.existsByUsernameAndIdNot("joao.silva", id)).thenReturn(false);
		when(perfilRepository.findByNome("Operador")).thenReturn(null);

		assertThrows(IllegalArgumentException.class, () -> usuarioDomainService.editarUsuario(id, request));

		verify(usuarioRepository, never()).save(any());
	}

	@Test
	void editarUsuario_deveEditar_quandoValido() {

		var id = UUID.randomUUID();
		var usuario = new Usuario();
		usuario.setId(id);
		usuario.setNome("João");
		usuario.setSobrenome("Silva");
		usuario.setUsername("joao.silva");
		usuario.setEmail("joao@teste.com");
		usuario.setPerfil(perfil);

		var request = new EditarUsuarioRequestDto();
		request.setNome("João");
		request.setSobrenome("Souza");
		request.setUsername("joao.souza");
		request.setEmail("joao.souza@teste.com");
		request.setPerfil("Operador");

		when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
		when(usuarioRepository.existsByEmailAndIdNot("joao.souza@teste.com", id)).thenReturn(false);
		when(usuarioRepository.existsByUsernameAndIdNot("joao.souza", id)).thenReturn(false);
		when(perfilRepository.findByNome("Operador")).thenReturn(perfil);

		var response = usuarioDomainService.editarUsuario(id, request);

		assertEquals(id, response.getId());
		assertEquals("Souza", response.getSobrenome());
		assertEquals("joao.souza", response.getUsername());
		assertEquals("joao.souza@teste.com", response.getEmail());
		assertEquals("Operador", response.getPerfil());
		verify(usuarioRepository, times(1)).save(usuario);
	}

	@Test
	void atualizarStatusUsuario_deveLancarExcecao_quandoUsuarioNaoEncontrado() {

		var id = UUID.randomUUID();
		var request = new AtualizarStatusUsuarioRequestDto();
		request.setAtivo(false);

		when(usuarioRepository.findById(id)).thenReturn(Optional.empty());

		assertThrows(EntityNotFoundException.class, () -> usuarioDomainService.atualizarStatusUsuario(id, request));

		verify(usuarioRepository, never()).save(any());
	}

	@Test
	void atualizarStatusUsuario_deveInativar_quandoValido() {

		var id = UUID.randomUUID();
		var usuario = new Usuario();
		usuario.setId(id);
		usuario.setPerfil(perfil);

		var request = new AtualizarStatusUsuarioRequestDto();
		request.setAtivo(false);

		when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));

		var response = usuarioDomainService.atualizarStatusUsuario(id, request);

		assertEquals(id, response.getId());
		assertEquals(false, response.isAtivo());
		verify(usuarioRepository, times(1)).save(usuario);
	}
}
