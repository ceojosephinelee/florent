package com.florent.support;

import com.florent.common.security.UserPrincipal;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.List;

public class WithMockBuyerSecurityContextFactory implements WithSecurityContextFactory<WithMockBuyer> {

    @Override
    public SecurityContext createSecurityContext(WithMockBuyer annotation) {
        UserPrincipal principal = new UserPrincipal(
                annotation.userId(),
                annotation.buyerId(),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_BUYER")));

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        return context;
    }
}
