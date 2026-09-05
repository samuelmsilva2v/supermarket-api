package com.example.demo.infrastructure.components;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.demo.domain.models.entities.Usuario;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenComponent {

	@Value("${jwt.secret}")
	private String secretKey;

	/*
	 * Método para criar e retornar um TOKEN JWT para o usuário autenticado no
	 * sistema
	 */
	public String getToken(Usuario usuario) {

		return Jwts.builder().setSubject(usuario.getUsername()) // Username do usuário
				.claim("perfil", usuario.getPerfil().getNome()) // Perfil do usuário
				.setIssuedAt(new Date()) // Data de geração do token
				.setExpiration(new Date(System.currentTimeMillis() + 1800000)) // Data de expiração do token (30min)
				.signWith(SignatureAlgorithm.HS256, secretKey) // Chave para assinatura do token
				.compact();
	}
}
