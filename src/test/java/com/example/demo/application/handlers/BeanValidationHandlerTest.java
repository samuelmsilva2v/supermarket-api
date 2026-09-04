package com.example.demo.application.handlers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

class BeanValidationHandlerTest {

	@Test
	void handleValidationExceptions_deveRetornarMapaDeErrosPorCampo() {

		var handler = new BeanValidationHandler();

		var ex = mock(MethodArgumentNotValidException.class);
		var bindingResult = mock(BindingResult.class);

		when(ex.getBindingResult()).thenReturn(bindingResult);
		when(bindingResult.getFieldErrors()).thenReturn(
				List.of(new FieldError("request", "nome", "O nome da categoria é obrigatório.")));

		var response = handler.handleValidationExceptions(ex);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("O nome da categoria é obrigatório.", response.getBody().get("nome"));
	}
}
