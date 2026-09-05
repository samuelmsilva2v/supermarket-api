package com.example.demo.domain.services.interfaces;

import java.util.UUID;

import com.example.demo.application.dtos.AutenticarUsuarioRequestDto;
import com.example.demo.application.dtos.AutenticarUsuarioResponseDto;
import com.example.demo.application.dtos.CriarUsuarioRequestDto;
import com.example.demo.application.dtos.CriarUsuarioResponseDto;
import com.example.demo.application.dtos.EditarUsuarioRequestDto;
import com.example.demo.application.dtos.PaginaResponseDto;
import com.example.demo.application.dtos.UsuarioResponseDto;

public interface UsuarioDomainService {

	public CriarUsuarioResponseDto criarUsuario(CriarUsuarioRequestDto request);

	public AutenticarUsuarioResponseDto autenticarUsuario(AutenticarUsuarioRequestDto request);

	public PaginaResponseDto<UsuarioResponseDto> consultarUsuarios(String username, int pagina, int tamanho);

	public UsuarioResponseDto consultarUsuarioPorId(UUID id);

	public UsuarioResponseDto editarUsuario(UUID id, EditarUsuarioRequestDto request);

}
