package com.florent.support;

import com.florent.common.security.UserPrincipal;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.List;

public class WithMockSellerSecurityContextFactory implements WithSecurityContextFactory<WithMockSeller> {

    @Override
    public SecurityContext createSecurityContext(WithMockSeller annotation) {
        UserPrincipal principal = new UserPrincipal(
                annotation.userId(),
                null,
                annotation.sellerId(),
                List.of(new SimpleGrantedAuthority("ROLE_SELLER")));

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        return context;
    }
}
