package com.example.demo.application.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import com.example.demo.application.dtos.AtualizarStatusUsuarioRequestDto;
import com.example.demo.application.dtos.AutenticarUsuarioRequestDto;
import com.example.demo.application.dtos.AutenticarUsuarioResponseDto;
import com.example.demo.application.dtos.CriarUsuarioRequestDto;
import com.example.demo.application.dtos.CriarUsuarioResponseDto;
import com.example.demo.application.dtos.EditarUsuarioRequestDto;
import com.example.demo.application.dtos.PaginaResponseDto;
import com.example.demo.application.dtos.UsuarioResponseDto;
import com.example.demo.domain.services.interfaces.UsuarioDomainService;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

	@Mock
	private UsuarioDomainService usuarioDomainService;

	@InjectMocks
	private UsuarioController usuarioController;

	@Test
	void criar_deveCriarUsuario() {

		var request = new CriarUsuarioRequestDto();
		var response = new CriarUsuarioResponseDto();
		response.setUsername("joao.silva");

		when(usuarioDomainService.criarUsuario(request)).thenReturn(response);

		var result = usuarioController.criar(request);

		assertEquals(response, result);
		verify(usuarioDomainService, times(1)).criarUsuario(request);
	}

	@Test
	void autenticar_deveAutenticarUsuario() {

		var request = new AutenticarUsuarioRequestDto();
		var response = new AutenticarUsuarioResponseDto();
		response.setToken("token-jwt");

		when(usuarioDomainService.autenticarUsuario(request)).thenReturn(response);

		var result = usuarioController.autenticar(request);

		assertEquals(response, result);
		verify(usuarioDomainService, times(1)).autenticarUsuario(request);
	}

	@Test
	void getByUsername_deveConsultarUsuariosPaginado() {

		var response = new PaginaResponseDto<UsuarioResponseDto>(Page.empty());

		when(usuarioDomainService.consultarUsuarios("joao", 0, 10)).thenReturn(response);

		var result = usuarioController.getByUsername("joao", 0, 10);

		assertEquals(response, result);
		verify(usuarioDomainService, times(1)).consultarUsuarios("joao", 0, 10);
	}

	@Test
	void put_deveEditarUsuario() {

		var id = UUID.randomUUID();
		var request = new EditarUsuarioRequestDto();
		var response = new UsuarioResponseDto();
		response.setId(id);

		when(usuarioDomainService.editarUsuario(id, request)).thenReturn(response);

		var result = usuarioController.put(id, request);

		assertEquals(response, result);
		verify(usuarioDomainService, times(1)).editarUsuario(id, request);
	}

	@Test
	void atualizarStatus_deveAtualizarStatusDeUsuario() {

		var id = UUID.randomUUID();
		var request = new AtualizarStatusUsuarioRequestDto();
		request.setAtivo(false);
		var response = new UsuarioResponseDto();
		response.setId(id);

		when(usuarioDomainService.atualizarStatusUsuario(id, request)).thenReturn(response);

		var result = usuarioController.atualizarStatus(id, request);

		assertEquals(response, result);
		verify(usuarioDomainService, times(1)).atualizarStatusUsuario(id, request);
	}
}
