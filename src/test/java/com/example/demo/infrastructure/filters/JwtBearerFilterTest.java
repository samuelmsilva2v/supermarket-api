package com.example.demo.infrastructure.filters;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class JwtBearerFilterTest {

	private static final String SECRET_KEY = "468041be-f345-4188-8136-4bdeef74b376";

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@Mock
	private FilterChain filterChain;

	private JwtBearerFilter filter;

	@BeforeEach
	void setUp() {
		filter = new JwtBearerFilter(SECRET_KEY);
	}

	private String buildToken(String perfil, long expirationOffsetMillis) {
		return Jwts.builder().setSubject("joao.silva").claim("perfil", perfil).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + expirationOffsetMillis))
				.signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
	}

	@Test
	void doFilter_deveLiberarRequisicaoOptions() throws Exception {

		when(request.getMethod()).thenReturn("OPTIONS");

		filter.doFilter(request, response, filterChain);

		verify(response, times(1)).setStatus(HttpServletResponse.SC_OK);
		verify(filterChain, times(1)).doFilter(request, response);
		verify(response, never()).sendError(anyInt(), anyString());
	}

	@Test
	void doFilter_deveLiberarRotaDeAutenticacaoSemToken() throws Exception {

		when(request.getMethod()).thenReturn("POST");
		when(request.getRequestURI()).thenReturn("/api/usuario/autenticar");

		filter.doFilter(request, response, filterChain);

		verify(filterChain, times(1)).doFilter(request, response);
		verify(response, never()).sendError(anyInt(), anyString());
	}

	@Test
	void doFilter_deveRetornar401_quandoSemHeaderAuthorization() throws Exception {

		when(request.getMethod()).thenReturn("GET");
		when(request.getRequestURI()).thenReturn("/api/produtos");
		when(request.getHeader("Authorization")).thenReturn(null);

		filter.doFilter(request, response, filterChain);

		verify(response, times(1)).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Acesso não autorizado.");
		verify(filterChain, never()).doFilter(any(), any());
	}

	@Test
	void doFilter_deveRetornar401_quandoHeaderNaoComecaComBearer() throws Exception {

		when(request.getMethod()).thenReturn("GET");
		when(request.getRequestURI()).thenReturn("/api/produtos");
		when(request.getHeader("Authorization")).thenReturn("Basic algumacoisa");

		filter.doFilter(request, response, filterChain);

		verify(response, times(1)).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Acesso não autorizado.");
		verify(filterChain, never()).doFilter(any(), any());
	}

	@Test
	void doFilter_deveLiberarRequisicao_quandoTokenValidoERotaComum() throws Exception {

		var token = buildToken("Operador", 1_800_000);

		when(request.getMethod()).thenReturn("GET");
		when(request.getRequestURI()).thenReturn("/api/produtos");
		when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

		filter.doFilter(request, response, filterChain);

		verify(request, times(1)).setAttribute(eq("claims"), any());
		verify(filterChain, times(1)).doFilter(request, response);
		verify(response, never()).sendError(anyInt(), anyString());
	}

	@Test
	void doFilter_deveLiberarCriacaoDeUsuario_quandoPerfilAdministrador() throws Exception {

		var token = buildToken("Administrador", 1_800_000);

		when(request.getMethod()).thenReturn("POST");
		when(request.getRequestURI()).thenReturn("/api/usuario/criar");
		when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

		filter.doFilter(request, response, filterChain);

		verify(filterChain, times(1)).doFilter(request, response);
		verify(response, never()).sendError(anyInt(), anyString());
	}

	@Test
	void doFilter_deveRetornar403_quandoCriarUsuarioComPerfilNaoAdministrador() throws Exception {

		var token = buildToken("Operador", 1_800_000);

		when(request.getMethod()).thenReturn("POST");
		when(request.getRequestURI()).thenReturn("/api/usuario/criar");
		when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

		filter.doFilter(request, response, filterChain);

		verify(response, times(1)).sendError(HttpServletResponse.SC_FORBIDDEN,
				"Acesso restrito a administradores.");
		verify(filterChain, never()).doFilter(any(), any());
	}

	@Test
	void doFilter_deveLiberarConsultaDeUsuarios_quandoPerfilAdministrador() throws Exception {

		var token = buildToken("Administrador", 1_800_000);

		when(request.getMethod()).thenReturn("GET");
		when(request.getRequestURI()).thenReturn("/api/usuario");
		when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

		filter.doFilter(request, response, filterChain);

		verify(filterChain, times(1)).doFilter(request, response);
		verify(response, never()).sendError(anyInt(), anyString());
	}

	@Test
	void doFilter_deveRetornar403_quandoConsultarUsuariosComPerfilNaoAdministrador() throws Exception {

		var token = buildToken("Operador", 1_800_000);

		when(request.getMethod()).thenReturn("GET");
		when(request.getRequestURI()).thenReturn("/api/usuario");
		when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

		filter.doFilter(request, response, filterChain);

		verify(response, times(1)).sendError(HttpServletResponse.SC_FORBIDDEN,
				"Acesso restrito a administradores.");
		verify(filterChain, never()).doFilter(any(), any());
	}

	@Test
	void doFilter_deveLiberarEdicaoDeUsuario_quandoPerfilAdministrador() throws Exception {

		var token = buildToken("Administrador", 1_800_000);

		when(request.getMethod()).thenReturn("PUT");
		when(request.getRequestURI()).thenReturn("/api/usuario/" + UUID.randomUUID());
		when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

		filter.doFilter(request, response, filterChain);

		verify(filterChain, times(1)).doFilter(request, response);
		verify(response, never()).sendError(anyInt(), anyString());
	}

	@Test
	void doFilter_deveRetornar403_quandoEditarUsuarioComPerfilNaoAdministrador() throws Exception {

		var token = buildToken("Operador", 1_800_000);

		when(request.getMethod()).thenReturn("PUT");
		when(request.getRequestURI()).thenReturn("/api/usuario/" + UUID.randomUUID());
		when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

		filter.doFilter(request, response, filterChain);

		verify(response, times(1)).sendError(HttpServletResponse.SC_FORBIDDEN,
				"Acesso restrito a administradores.");
		verify(filterChain, never()).doFilter(any(), any());
	}

	@Test
	void doFilter_deveLiberarAtualizacaoDeStatusDeUsuario_quandoPerfilAdministrador() throws Exception {

		var token = buildToken("Administrador", 1_800_000);

		when(request.getMethod()).thenReturn("PATCH");
		when(request.getRequestURI()).thenReturn("/api/usuario/" + UUID.randomUUID() + "/status");
		when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

		filter.doFilter(request, response, filterChain);

		verify(filterChain, times(1)).doFilter(request, response);
		verify(response, never()).sendError(anyInt(), anyString());
	}

	@Test
	void doFilter_deveRetornar403_quandoAtualizarStatusDeUsuarioComPerfilNaoAdministrador() throws Exception {

		var token = buildToken("Operador", 1_800_000);

		when(request.getMethod()).thenReturn("PATCH");
		when(request.getRequestURI()).thenReturn("/api/usuario/" + UUID.randomUUID() + "/status");
		when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

		filter.doFilter(request, response, filterChain);

		verify(response, times(1)).sendError(HttpServletResponse.SC_FORBIDDEN,
				"Acesso restrito a administradores.");
		verify(filterChain, never()).doFilter(any(), any());
	}

	@Test
	void doFilter_deveRetornar401_quandoTokenExpiradoOuInvalido() throws Exception {

		var tokenExpirado = buildToken("Operador", -1_000);

		when(request.getMethod()).thenReturn("GET");
		when(request.getRequestURI()).thenReturn("/api/produtos");
		when(request.getHeader("Authorization")).thenReturn("Bearer " + tokenExpirado);

		filter.doFilter(request, response, filterChain);

		verify(response, times(1)).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token inválido ou expirado.");
		verify(filterChain, never()).doFilter(any(), any());
	}
}
