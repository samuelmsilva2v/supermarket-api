package com.example.demo.infrastructure.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.application.dtos.DashboardResponseDto;
import com.example.demo.domain.models.entities.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, UUID> {

	Categoria findByNome(String nome);

	boolean existsByNome(String nome);

	Page<Categoria> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

	@Query(value = """
	        SELECT
	            c.nome as nomeCategoria,
	            COALESCE(SUM(p.quantidade), 0) as qtdProdutos
	        FROM categoria c
	        LEFT JOIN produto p ON p.categoria_id = c.id
	        GROUP BY c.nome
	    """, nativeQuery = true)
	List<DashboardResponseDto> searchQuantityByCategory();
}
