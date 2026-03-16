package com.florent.support;

import com.florent.common.security.UserPrincipal;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.ArrayList;
import java.util.List;

public class WithMockAuthUserSecurityContextFactory implements WithSecurityContextFactory<WithMockAuthUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockAuthUser annotation) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        if (!annotation.role().isEmpty()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + annotation.role()));
        }

        UserPrincipal principal = new UserPrincipal(
                annotation.userId(),
                annotation.buyerId() == 0 ? null : annotation.buyerId(),
                annotation.sellerId() == 0 ? null : annotation.sellerId(),
                authorities);

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        return context;
    }
}
