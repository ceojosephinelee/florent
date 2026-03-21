package com.florent.common.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.florent.common.security.JwtProvider;
import com.florent.common.security.UserPrincipal;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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

/**
 * 로컬 개발용 인증 필터.
 * 1) 실제 JWT 토큰 → JwtProvider로 검증
 * 2) Base64 JSON 토큰 → 레거시 방식으로 파싱 (Swagger/테스트 편의용)
 */
@Slf4j
@Component
@Profile({"local", "prod"})
@RequiredArgsConstructor
public class DevAuthFilter extends OncePerRequestFilter {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            // JWT는 header.payload.signature 형식 (점 2개)
            if (token.chars().filter(c -> c == '.').count() == 2) {
                authenticateWithJwt(token);
            } else {
                authenticateWithBase64Json(token);
            }
        }
        filterChain.doFilter(request, response);
    }

    private void authenticateWithJwt(String token) {
        try {
            Claims claims = jwtProvider.validateAndExtractClaims(token);
            Long userId = Long.valueOf(claims.getSubject());
            String role = claims.get("role", String.class);
            Long buyerId = claims.get("buyerId", Long.class);
            Long sellerId = claims.get("sellerId", Long.class);

            setAuthentication(userId, role, buyerId, sellerId);
        } catch (Exception e) {
            log.debug("JWT 검증 실패: {}", e.getMessage());
        }
    }

    private void authenticateWithBase64Json(String token) {
        try {
            String json = new String(Base64.getDecoder().decode(token));
            JsonNode node = MAPPER.readTree(json);

            Long userId = node.get("userId").asLong();
            String role = node.get("role").asText();
            Long buyerId = node.has("buyerId") ? node.get("buyerId").asLong() : null;
            Long sellerId = node.has("sellerId") ? node.get("sellerId").asLong() : null;

            setAuthentication(userId, role, buyerId, sellerId);
        } catch (Exception e) {
            log.debug("Base64 JSON 토큰 파싱 실패: {}", e.getMessage());
        }
    }

    private void setAuthentication(Long userId, String role, Long buyerId, Long sellerId) {
        UserPrincipal principal = new UserPrincipal(
                userId, buyerId, sellerId,
                List.of(new SimpleGrantedAuthority("ROLE_" + role)));
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
