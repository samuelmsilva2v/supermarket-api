package com.example.demo.infrastructure.components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.demo.domain.models.entities.Perfil;
import com.example.demo.domain.models.entities.Usuario;
import com.example.demo.infrastructure.repositories.PerfilRepository;
import com.example.demo.infrastructure.repositories.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class LoadDataComponentTest {

	private static final UUID ID_ADMINISTRADOR = UUID.fromString("cfd306c0-c516-4176-a215-bb7a49e54c6f");
	private static final UUID ID_OPERADOR = UUID.fromString("7f55d810-f21a-4052-9d39-6ef61cbe85b2");

	@Mock
	private PerfilRepository perfilRepository;

	@Mock
	private UsuarioRepository usuarioRepository;

	@Mock
	private SHA256Component sha256Component;

	@InjectMocks
	private LoadDataComponent loadDataComponent;

	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(loadDataComponent, "adminNome", "Administrador");
		ReflectionTestUtils.setField(loadDataComponent, "adminSobrenome", "Sistema");
		ReflectionTestUtils.setField(loadDataComponent, "adminUsername", "admin");
		ReflectionTestUtils.setField(loadDataComponent, "adminEmail", "admin@supermarket.com");
		ReflectionTestUtils.setField(loadDataComponent, "adminSenha", "Admin@123");
	}

	@Test
	void run_deveSemearPerfisEUsuarioAdmin_quandoNadaExiste() throws Exception {

		when(perfilRepository.existsById(any(UUID.class))).thenReturn(false);
		when(usuarioRepository.findByUsername("admin")).thenReturn(null);
		when(sha256Component.encrypt("Admin@123")).thenReturn("hash-admin");
		when(perfilRepository.findByNome("Administrador")).thenReturn(new Perfil());

		loadDataComponent.run(null);

		var perfilCaptor = ArgumentCaptor.forClass(Perfil.class);
		verify(perfilRepository, times(2)).save(perfilCaptor.capture());

		var perfisSalvos = perfilCaptor.getAllValues();
		assertEquals(ID_ADMINISTRADOR, perfisSalvos.get(0).getId());
		assertEquals("Administrador", perfisSalvos.get(0).getNome());
		assertEquals(ID_OPERADOR, perfisSalvos.get(1).getId());
		assertEquals("Operador", perfisSalvos.get(1).getNome());

		var usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
		verify(usuarioRepository, times(1)).save(usuarioCaptor.capture());
		assertEquals("admin", usuarioCaptor.getValue().getUsername());
		assertEquals("hash-admin", usuarioCaptor.getValue().getSenha());
	}

	@Test
	void run_naoDeveSemearPerfis_quandoJaExistem() throws Exception {

		when(perfilRepository.existsById(any(UUID.class))).thenReturn(true);
		when(usuarioRepository.findByUsername("admin")).thenReturn(null);
		when(sha256Component.encrypt("Admin@123")).thenReturn("hash-admin");
		when(perfilRepository.findByNome("Administrador")).thenReturn(new Perfil());

		loadDataComponent.run(null);

		verify(perfilRepository, never()).save(any());
		verify(usuarioRepository, times(1)).save(any(Usuario.class));
	}

	@Test
	void run_naoDeveSemearUsuarioAdmin_quandoJaExiste() throws Exception {

		when(perfilRepository.existsById(any(UUID.class))).thenReturn(true);
		when(usuarioRepository.findByUsername("admin")).thenReturn(new Usuario());

		loadDataComponent.run(null);

		verify(perfilRepository, never()).save(any());
		verify(usuarioRepository, never()).save(any());
		verify(perfilRepository, never()).findByNome(eq("Administrador"));
	}
}
