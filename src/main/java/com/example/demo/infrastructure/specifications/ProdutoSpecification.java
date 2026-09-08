package com.example.demo.infrastructure.specifications;

import org.springframework.data.jpa.domain.Specification;

import com.example.demo.application.dtos.ProdutoFiltroRequestDto;
import com.example.demo.domain.models.entities.Produto;

import jakarta.persistence.criteria.JoinType;

public final class ProdutoSpecification {

	private ProdutoSpecification() {
	}

	public static Specification<Produto> filtrar(ProdutoFiltroRequestDto filtro) {
		return new GenericSpecificationBuilder<Produto>()
				.comTextoContendo("nome", filtro.getNome())
				.comMaiorOuIgual("preco", filtro.getPrecoMin())
				.comMenorOuIgual("preco", filtro.getPrecoMax())
				.comMaiorOuIgual("quantidade", filtro.getQuantidadeMin())
				.comMenorOuIgual("quantidade", filtro.getQuantidadeMax())
				.comIgualdade("unidadeMedida", filtro.getUnidadeMedida())
				.comIgualdade("categoria.id", filtro.getCategoriaId())
				.comEspecificacao(FETCH_CATEGORIA)
				.build();
	}

	// Evita N+1 ao carregar a categoria de cada produto na listagem, sem afetar a query de contagem da paginação.
	private static final Specification<Produto> FETCH_CATEGORIA = (root, query, cb) -> {
		if (Long.class != query.getResultType() && long.class != query.getResultType()) {
			root.fetch("categoria", JoinType.LEFT);
		}
		return cb.conjunction();
	};
}
