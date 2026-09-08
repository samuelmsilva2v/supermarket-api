package com.example.demo.infrastructure.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.example.demo.domain.models.entities.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, UUID>, JpaSpecificationExecutor<Produto> {

	boolean existsByCategoriaId(UUID categoriaId);

	boolean existsByNome(String nome);

	boolean existsByNomeAndIdNot(String nome, UUID id);
}
