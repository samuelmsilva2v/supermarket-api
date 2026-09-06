package com.example.demo.infrastructure.components;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.jsonwebtoken.Claims;

@Component
public class JwtAuditorAware implements AuditorAware<String> {

	@Override
	public Optional<String> getCurrentAuditor() {

		if (!(RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attrs))
			return Optional.empty();

		var claims = (Claims) attrs.getRequest().getAttribute("claims");

		return claims == null ? Optional.empty() : Optional.of(claims.getSubject());
	}
}
