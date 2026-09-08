package com.example.demo.infrastructure.specifications;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

public class GenericSpecificationBuilder<T> {

	private final List<Specification<T>> especificacoes = new ArrayList<>();

	public GenericSpecificationBuilder<T> comIgualdade(String campo, Object valor) {
		if (valor != null) {
			especificacoes.add((root, query, cb) -> cb.equal(caminho(root, campo), valor));
		}
		return this;
	}

	public GenericSpecificationBuilder<T> comTextoContendo(String campo, String valor) {
		if (StringUtils.hasText(valor)) {
			var valorBusca = "%" + valor.toLowerCase() + "%";
			especificacoes.add((root, query, cb) -> cb.like(cb.lower(this.<String>caminho(root, campo)), valorBusca));
		}
		return this;
	}

	public GenericSpecificationBuilder<T> comTextoContendoEmAlgumCampo(String valor, String... campos) {
		if (StringUtils.hasText(valor) && campos.length > 0) {
			var valorBusca = "%" + valor.toLowerCase() + "%";
			especificacoes.add((root, query, cb) -> {
				var predicados = Arrays.stream(campos)
						.map(campo -> cb.like(cb.lower(this.<String>caminho(root, campo)), valorBusca))
						.toArray(Predicate[]::new);
				return cb.or(predicados);
			});
		}
		return this;
	}

	public <Y extends Comparable<? super Y>> GenericSpecificationBuilder<T> comMaiorOuIgual(String campo, Y valor) {
		if (valor != null) {
			especificacoes.add((root, query, cb) -> cb.greaterThanOrEqualTo(caminho(root, campo), valor));
		}
		return this;
	}

	public <Y extends Comparable<? super Y>> GenericSpecificationBuilder<T> comMenorOuIgual(String campo, Y valor) {
		if (valor != null) {
			especificacoes.add((root, query, cb) -> cb.lessThanOrEqualTo(caminho(root, campo), valor));
		}
		return this;
	}

	public GenericSpecificationBuilder<T> comValorEm(String campo, Collection<?> valores) {
		if (valores != null && !valores.isEmpty()) {
			especificacoes.add((root, query, cb) -> caminho(root, campo).in(valores));
		}
		return this;
	}

	public GenericSpecificationBuilder<T> comEspecificacao(Specification<T> especificacao) {
		if (especificacao != null) {
			especificacoes.add(especificacao);
		}
		return this;
	}

	public Specification<T> build() {
		return especificacoes.stream().reduce(Specification.where(null), Specification::and);
	}

	@SuppressWarnings("unchecked")
	private <Y> Path<Y> caminho(Path<?> origem, String campo) {
		Path<?> caminho = origem;
		for (String parte : campo.split("\\.")) {
			caminho = caminho.get(parte);
		}
		return (Path<Y>) caminho;
	}
}
