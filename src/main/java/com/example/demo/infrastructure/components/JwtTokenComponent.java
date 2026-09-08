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

	public String getToken(Usuario usuario) {

		return Jwts.builder().setSubject(usuario.getUsername())
				.claim("perfil", usuario.getPerfil().getNome())
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + 1800000)) // 30 min
				.signWith(SignatureAlgorithm.HS256, secretKey)
				.compact();
	}
}
