package br.com.fourteca.service;

import br.com.fourteca.entity.Usuario;
import br.com.fourteca.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private AuthService authService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setLogin("admin");
        usuario.setSenha("encoded_password");
    }

    @Test
    void deveCarregarUsuarioPorUsernameComSucesso() {
        // Cenário
        when(usuarioRepository.findByLogin("admin")).thenReturn(Optional.of(usuario));

        // Ação
        UserDetails userDetails = authService.loadUserByUsername("admin");

        // Validação
        assertNotNull(userDetails);
        assertEquals("admin", userDetails.getUsername());
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        // Cenário
        when(usuarioRepository.findByLogin("unknown_user")).thenReturn(Optional.empty());

        // Ação e Validação
        assertThrows(UsernameNotFoundException.class, () -> {
            authService.loadUserByUsername("unknown_user");
        });
    }
}
