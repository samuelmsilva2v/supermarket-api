package com.example.demo.application.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.application.dtos.AtualizarStatusUsuarioRequestDto;
import com.example.demo.application.dtos.AutenticarUsuarioRequestDto;
import com.example.demo.application.dtos.AutenticarUsuarioResponseDto;
import com.example.demo.application.dtos.CriarUsuarioRequestDto;
import com.example.demo.application.dtos.CriarUsuarioResponseDto;
import com.example.demo.application.dtos.EditarUsuarioRequestDto;
import com.example.demo.application.dtos.PaginaResponseDto;
import com.example.demo.application.dtos.UsuarioResponseDto;
import com.example.demo.domain.services.interfaces.UsuarioDomainService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/usuario")
public class UsuarioController {

	@Autowired
	private UsuarioDomainService usuarioDomainService;

	@Operation(summary = "Serviço para criação de usuários.")
	@PostMapping("criar")
	public CriarUsuarioResponseDto criar(@RequestBody @Valid CriarUsuarioRequestDto request) {
		return usuarioDomainService.criarUsuario(request);
	}

	@Operation(summary = "Serviço para autenticação de usuários.")
	@PostMapping("autenticar")
	public AutenticarUsuarioResponseDto autenticar(@RequestBody @Valid AutenticarUsuarioRequestDto request) {
		return usuarioDomainService.autenticarUsuario(request);
	}

	@Operation(summary = "Serviço para consultar usuários por username, paginado.")
	@GetMapping
	public PaginaResponseDto<UsuarioResponseDto> getByUsername(
			@RequestParam(defaultValue = "") String username,
			@RequestParam(defaultValue = "0") int pagina,
			@RequestParam(defaultValue = "10") int tamanho) {
		return usuarioDomainService.consultarUsuarios(username, pagina, tamanho);
	}

	@Operation(summary = "Serviço para consultar um usuário por ID.")
	@GetMapping("/{id}")
	public UsuarioResponseDto getById(@PathVariable UUID id) {
		return usuarioDomainService.consultarUsuarioPorId(id);
	}

	@Operation(summary = "Serviço para editar um usuário.")
	@PutMapping("/{id}")
	public UsuarioResponseDto put(@PathVariable UUID id, @RequestBody @Valid EditarUsuarioRequestDto request) {
		return usuarioDomainService.editarUsuario(id, request);
	}

	@Operation(summary = "Serviço para ativar/inativar um usuário.")
	@PatchMapping("/{id}/status")
	public UsuarioResponseDto atualizarStatus(@PathVariable UUID id,
			@RequestBody @Valid AtualizarStatusUsuarioRequestDto request) {
		return usuarioDomainService.atualizarStatusUsuario(id, request);
	}
}
