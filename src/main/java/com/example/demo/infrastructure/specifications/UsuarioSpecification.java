package com.example.demo.infrastructure.specifications;

import org.springframework.data.jpa.domain.Specification;

import com.example.demo.application.dtos.UsuarioFiltroRequestDto;
import com.example.demo.domain.models.entities.Usuario;

public final class UsuarioSpecification {

	private UsuarioSpecification() {
	}

	public static Specification<Usuario> filtrar(UsuarioFiltroRequestDto filtro) {
		return new GenericSpecificationBuilder<Usuario>()
				.comTextoContendo("username", filtro.getUsername())
				.comTextoContendoEmAlgumCampo(filtro.getNome(), "nome", "sobrenome")
				.comIgualdade("ativo", filtro.getAtivo())
				.comIgualdade("perfil.nome", filtro.getPerfil())
				.build();
	}
}
