package com.example.demo.domain.services.impl;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.application.dtos.AlterarSenhaRequestDto;
import com.example.demo.application.dtos.AtualizarStatusUsuarioRequestDto;
import com.example.demo.application.dtos.AutenticarUsuarioRequestDto;
import com.example.demo.application.dtos.AutenticarUsuarioResponseDto;
import com.example.demo.application.dtos.CriarUsuarioRequestDto;
import com.example.demo.application.dtos.CriarUsuarioResponseDto;
import com.example.demo.application.dtos.EditarPerfilPropioRequestDto;
import com.example.demo.application.dtos.EditarUsuarioRequestDto;
import com.example.demo.application.dtos.EsqueciSenhaRequestDto;
import com.example.demo.application.dtos.PaginaResponseDto;
import com.example.demo.application.dtos.UsuarioFiltroRequestDto;
import com.example.demo.application.dtos.UsuarioResponseDto;
import com.example.demo.domain.exceptions.CredenciaisInvalidasException;
import com.example.demo.domain.exceptions.SenhaAtualInvalidaException;
import com.example.demo.domain.exceptions.UsuarioComEmailDuplicadoException;
import com.example.demo.domain.exceptions.UsuarioComUsernameDuplicadoException;
import com.example.demo.domain.exceptions.UsuarioInativoException;
import com.example.demo.domain.models.entities.Usuario;
import com.example.demo.domain.services.interfaces.UsuarioDomainService;
import com.example.demo.infrastructure.components.JwtTokenComponent;
import com.example.demo.infrastructure.messaging.RedefinicaoSenhaEvento;
import com.example.demo.infrastructure.messaging.RedefinicaoSenhaProducer;
import com.example.demo.infrastructure.repositories.PerfilRepository;
import com.example.demo.infrastructure.repositories.UsuarioRepository;
import com.example.demo.infrastructure.specifications.UsuarioSpecification;

import jakarta.persistence.EntityNotFoundException;

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

	@Autowired
	private RedefinicaoSenhaProducer redefinicaoSenhaProducer;

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

		if (!usuario.isAtivo())
			throw new UsuarioInativoException();

		var response = new AutenticarUsuarioResponseDto();
		response.setId(usuario.getId());
		response.setNome(usuario.getNome());
		response.setUsername(usuario.getUsername());
		response.setEmail(usuario.getEmail());
		response.setPerfil(usuario.getPerfil().getNome());
		response.setToken(jwtTokenComponent.getToken(usuario));

		return response;
	}

	@Override
	public PaginaResponseDto<UsuarioResponseDto> consultarUsuariosPaginado(UsuarioFiltroRequestDto filtro, int pagina,
			int tamanho) {
		var pageable = PageRequest.of(pagina, tamanho, Sort.by("username"));
		var especificacao = UsuarioSpecification.filtrar(filtro);
		var usuariosPage = usuarioRepository.findAll(especificacao, pageable);
		return new PaginaResponseDto<>(usuariosPage.map(this::toUsuarioResponseDto));
	}

	@Override
	public UsuarioResponseDto consultarUsuarioPorId(UUID id) {

		var usuario = usuarioRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Usuário com ID " + id + " não encontrado."));

		return toUsuarioResponseDto(usuario);
	}

	@Override
	public UsuarioResponseDto editarUsuario(UUID id, EditarUsuarioRequestDto request) {

		var usuario = usuarioRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Usuário com ID " + id + " não encontrado."));

		if (usuarioRepository.existsByEmailAndIdNot(request.getEmail(), id))
			throw new UsuarioComEmailDuplicadoException();

		if (usuarioRepository.existsByUsernameAndIdNot(request.getUsername(), id))
			throw new UsuarioComUsernameDuplicadoException();

		var perfil = perfilRepository.findByNome(request.getPerfil());
		if (perfil == null)
			throw new IllegalArgumentException("Perfil informado é inválido.");

		usuario.setNome(request.getNome());
		usuario.setSobrenome(request.getSobrenome());
		usuario.setUsername(request.getUsername());
		usuario.setEmail(request.getEmail());
		usuario.setPerfil(perfil);

		usuarioRepository.save(usuario);

		return toUsuarioResponseDto(usuario);
	}

	@Override
	public UsuarioResponseDto atualizarStatusUsuario(UUID id, AtualizarStatusUsuarioRequestDto request) {

		var usuario = usuarioRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Usuário com ID " + id + " não encontrado."));

		usuario.setAtivo(request.getAtivo());

		usuarioRepository.save(usuario);

		return toUsuarioResponseDto(usuario);
	}

	@Override
	public UsuarioResponseDto consultarPerfilProprio(String username) {

		var usuario = usuarioRepository.findByUsername(username);
		if (usuario == null)
			throw new EntityNotFoundException("Usuário com username " + username + " não encontrado.");

		return toUsuarioResponseDto(usuario);
	}

	@Override
	public UsuarioResponseDto editarPerfilProprio(String username, EditarPerfilPropioRequestDto request) {

		var usuario = usuarioRepository.findByUsername(username);
		if (usuario == null)
			throw new EntityNotFoundException("Usuário com username " + username + " não encontrado.");

		if (usuarioRepository.existsByEmailAndIdNot(request.getEmail(), usuario.getId()))
			throw new UsuarioComEmailDuplicadoException();

		usuario.setNome(request.getNome());
		usuario.setSobrenome(request.getSobrenome());
		usuario.setEmail(request.getEmail());

		usuarioRepository.save(usuario);

		return toUsuarioResponseDto(usuario);
	}

	@Override
	public UsuarioResponseDto alterarSenha(String username, AlterarSenhaRequestDto request) {

		var usuario = usuarioRepository.findByUsername(username);
		if (usuario == null)
			throw new EntityNotFoundException("Usuário com username " + username + " não encontrado.");

		if (!passwordEncoder.matches(request.getSenhaAtual(), usuario.getSenha()))
			throw new SenhaAtualInvalidaException();

		usuario.setSenha(passwordEncoder.encode(request.getNovaSenha()));

		usuarioRepository.save(usuario);

		return toUsuarioResponseDto(usuario);
	}

	@Override
	public void esqueciSenha(EsqueciSenhaRequestDto request) {

		var usuario = usuarioRepository.findByEmail(request.getEmail());
		if (usuario == null)
			throw new EntityNotFoundException("Nenhum usuário encontrado com o e-mail informado.");

		var novaSenha = gerarNovaSenha();

		usuario.setSenha(passwordEncoder.encode(novaSenha));
		usuarioRepository.save(usuario);

		var evento = new RedefinicaoSenhaEvento();
		evento.setNome(usuario.getNome());
		evento.setEmail(usuario.getEmail());
		evento.setNovaSenha(novaSenha);

		redefinicaoSenhaProducer.publicar(evento);
	}

	// Gera uma senha aleatória atendendo aos mesmos critérios de complexidade exigidos no cadastro,
	// evitando caracteres ambíguos (I, O, l, 0, 1) para facilitar a leitura no e-mail
	private String gerarNovaSenha() {

		var maiusculas = "ABCDEFGHJKLMNPQRSTUVWXYZ";
		var minusculas = "abcdefghijkmnpqrstuvwxyz";
		var numeros = "23456789";
		var simbolos = "@#$%^&+=!";
		var todos = maiusculas + minusculas + numeros + simbolos;

		var random = new SecureRandom();
		var caracteres = new ArrayList<Character>();
		caracteres.add(maiusculas.charAt(random.nextInt(maiusculas.length())));
		caracteres.add(minusculas.charAt(random.nextInt(minusculas.length())));
		caracteres.add(numeros.charAt(random.nextInt(numeros.length())));
		caracteres.add(simbolos.charAt(random.nextInt(simbolos.length())));

		for (int i = caracteres.size(); i < 12; i++)
			caracteres.add(todos.charAt(random.nextInt(todos.length())));

		Collections.shuffle(caracteres, random);

		var senha = new StringBuilder();
		caracteres.forEach(senha::append);

		return senha.toString();
	}

	private UsuarioResponseDto toUsuarioResponseDto(Usuario usuario) {
		var dto = new UsuarioResponseDto();
		dto.setId(usuario.getId());
		dto.setNome(usuario.getNome());
		dto.setSobrenome(usuario.getSobrenome());
		dto.setUsername(usuario.getUsername());
		dto.setEmail(usuario.getEmail());
		dto.setPerfil(usuario.getPerfil().getNome());
		dto.setAtivo(usuario.isAtivo());
		dto.setCreatedAt(usuario.getCreatedAt());
		dto.setUpdatedAt(usuario.getUpdatedAt());
		dto.setCreatedBy(usuario.getCreatedBy());
		dto.setUpdatedBy(usuario.getUpdatedBy());
		return dto;
	}
}
