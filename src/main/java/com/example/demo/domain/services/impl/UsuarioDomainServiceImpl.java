package com.example.demo.domain.services.impl;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.application.dtos.AutenticarUsuarioRequestDto;
import com.example.demo.application.dtos.AutenticarUsuarioResponseDto;
import com.example.demo.application.dtos.CriarUsuarioRequestDto;
import com.example.demo.application.dtos.CriarUsuarioResponseDto;
import com.example.demo.domain.exceptions.CredenciaisInvalidasException;
import com.example.demo.domain.exceptions.UsuarioComEmailDuplicadoException;
import com.example.demo.domain.exceptions.UsuarioComUsernameDuplicadoException;
import com.example.demo.domain.models.entities.Usuario;
import com.example.demo.domain.services.interfaces.UsuarioDomainService;
import com.example.demo.infrastructure.components.JwtTokenComponent;
import com.example.demo.infrastructure.repositories.PerfilRepository;
import com.example.demo.infrastructure.repositories.UsuarioRepository;

@Service
public class UsuarioDomainServiceImpl implements UsuarioDomainService {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private PerfilRepository perfilRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JwtTokenComponent jwtTokenComponent;

	@Override
	public CriarUsuarioResponseDto criarUsuario(CriarUsuarioRequestDto request) {

		if (usuarioRepository.findByEmail(request.getEmail()) != null)
			throw new UsuarioComEmailDuplicadoException();

		if (usuarioRepository.findByUsername(request.getUsername()) != null)
			throw new UsuarioComUsernameDuplicadoException();

		var perfil = perfilRepository.findByNome(request.getPerfil());
		if (perfil == null)
			throw new IllegalArgumentException("Perfil informado é inválido.");

		var usuario = new Usuario();
		usuario.setId(UUID.randomUUID());
		usuario.setNome(request.getNome());
		usuario.setSobrenome(request.getSobrenome());
		usuario.setUsername(request.getUsername());
		usuario.setEmail(request.getEmail());
		usuario.setSenha(passwordEncoder.encode(request.getSenha()));
		usuario.setPerfil(perfil);

		usuarioRepository.save(usuario);

		var response = new CriarUsuarioResponseDto();
		response.setId(usuario.getId());
		response.setNome(usuario.getNome());
		response.setSobrenome(usuario.getSobrenome());
		response.setUsername(usuario.getUsername());
		response.setEmail(usuario.getEmail());
		response.setPerfil(usuario.getPerfil().getNome());
		response.setDataCriacao(Instant.now());

		return response;
	}

	@Override
	public AutenticarUsuarioResponseDto autenticarUsuario(AutenticarUsuarioRequestDto request) {

		var usuario = usuarioRepository.findByUsername(request.getUsername());

		if (usuario == null || !passwordEncoder.matches(request.getSenha(), usuario.getSenha()))
			throw new CredenciaisInvalidasException();

		var response = new AutenticarUsuarioResponseDto();
		response.setId(usuario.getId());
		response.setNome(usuario.getNome());
		response.setUsername(usuario.getUsername());
		response.setEmail(usuario.getEmail());
		response.setPerfil(usuario.getPerfil().getNome());
		response.setToken(jwtTokenComponent.getToken(usuario));

		return response;
	}
}
