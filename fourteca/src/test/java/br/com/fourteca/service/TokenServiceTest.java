package br.com.fourteca.service;

import br.com.fourteca.entity.Usuario;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @InjectMocks
    private TokenService tokenService;

    private Authentication authentication;
    private final String jwtSecret = "uma-chave-secreta-longa-o-suficiente-para-testes-de-jwt-hs256";
    private final long jwtExpiration = 86400000; // 24 horas

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(tokenService, "jwtSecret", jwtSecret);
        ReflectionTestUtils.setField(tokenService, "jwtExpiration", jwtExpiration);

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setLogin("testuser");
        authentication = new UsernamePasswordAuthenticationToken(usuario, null, Collections.emptyList());
    }

    @Test
    void deveGerarTokenComSucesso() {
        String token = tokenService.gerarToken(authentication);
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void deveValidarTokenComSucesso() {
        String token = tokenService.gerarToken(authentication);
        assertTrue(tokenService.isTokenValido(token));
    }

    @Test
    void deveInvalidarTokenExpirado() {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        String tokenExpirado = Jwts.builder()
                .setSubject("testuser")
                .setIssuedAt(new Date(System.currentTimeMillis() - 2000))
                .setExpiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(key)
                .compact();

        assertFalse(tokenService.isTokenValido(tokenExpirado));
    }

    @Test
    void deveInvalidarTokenComAssinaturaInvalida() {
        SecretKey invalidKey = Keys.hmacShaKeyFor("outra-chave-secreta-muito-longa-e-diferente-para-o-teste".getBytes(StandardCharsets.UTF_8));
        String token = Jwts.builder()
                .setSubject("testuser")
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(invalidKey)
                .compact();

        assertFalse(tokenService.isTokenValido(token));
    }

    @Test
    void deveObterSubjectDoToken() {
        String token = tokenService.gerarToken(authentication);
        String subject = tokenService.getSubject(token);
        assertEquals("testuser", subject);
    }
}
