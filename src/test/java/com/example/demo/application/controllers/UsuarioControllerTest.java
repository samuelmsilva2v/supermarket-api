package com.example.demo.application.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.application.dtos.AutenticarUsuarioRequestDto;
import com.example.demo.application.dtos.AutenticarUsuarioResponseDto;
import com.example.demo.application.dtos.CriarUsuarioRequestDto;
import com.example.demo.application.dtos.CriarUsuarioResponseDto;
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
}
