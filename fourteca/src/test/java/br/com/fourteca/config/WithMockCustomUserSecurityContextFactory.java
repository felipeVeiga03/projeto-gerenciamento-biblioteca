package br.com.fourteca.config;

import br.com.fourteca.entity.Usuario;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Collections;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        Usuario principal = new Usuario();
        principal.setId(customUser.id());
        principal.setLogin(customUser.login());

        Authentication auth = new UsernamePasswordAuthenticationToken(principal, "password", Collections.emptyList());
        context.setAuthentication(auth);
        return context;
    }
}
