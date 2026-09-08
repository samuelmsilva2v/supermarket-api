package com.example.demo.infrastructure.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.domain.models.entities.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID>, JpaSpecificationExecutor<Usuario> {

	@Query("SELECT u FROM Usuario u WHERE u.email = :email")
	Usuario findByEmail(@Param("email") String email);

	@Query("SELECT u FROM Usuario u WHERE u.username = :username")
	Usuario findByUsername(@Param("username") String username);

	boolean existsByEmailAndIdNot(String email, UUID id);

	boolean existsByUsernameAndIdNot(String username, UUID id);
}
