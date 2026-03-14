package com.florent.common.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.florent.common.security.UserPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Slf4j
@Component
@Profile("local")
public class DevAuthFilter extends OncePerRequestFilter {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                String json = new String(Base64.getDecoder().decode(token));
                JsonNode node = MAPPER.readTree(json);

                Long userId = node.get("userId").asLong();
                String role = node.get("role").asText();
                Long buyerId = node.has("buyerId") ? node.get("buyerId").asLong() : null;
                Long sellerId = node.has("sellerId") ? node.get("sellerId").asLong() : null;

                UserPrincipal principal = new UserPrincipal(
                        userId, buyerId, sellerId,
                        List.of(new SimpleGrantedAuthority("ROLE_" + role)));

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception e) {
                log.debug("토큰 파싱 실패 — unauthenticated 처리", e);
            }
        }
        filterChain.doFilter(request, response);
    }
}
