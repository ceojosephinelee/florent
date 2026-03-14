package com.florent.common.security;

import com.florent.common.exception.BusinessException;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@Profile("!local & !test")
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.equals("/api/v1/auth/kakao")
                || path.equals("/api/v1/auth/reissue")
                || path.equals("/actuator/health")
                || path.startsWith("/swagger-ui/")
                || path.startsWith("/v3/api-docs/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = extractBearerToken(request);
        if (token != null) {
            try {
                Claims claims = jwtProvider.validateAndExtractClaims(token);

                Long userId = Long.valueOf(claims.getSubject());
                String role = claims.get("role", String.class);
                Long buyerId = claims.get("buyerId", Long.class);
                Long sellerId = claims.get("sellerId", Long.class);

                UserPrincipal principal = new UserPrincipal(
                        userId, buyerId, sellerId,
                        List.of(new SimpleGrantedAuthority("ROLE_" + role)));

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (BusinessException e) {
                response.setStatus(e.getErrorCode().getHttpStatus());
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(
                        "{\"success\":false,\"error\":{\"code\":\"" + e.getErrorCode().name()
                                + "\",\"message\":\"" + e.getErrorCode().getMessage() + "\"}}");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private String extractBearerToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
