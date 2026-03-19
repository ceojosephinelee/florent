package com.florent.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.florent.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.florent.common.security.JwtAuthenticationFilter;

import java.util.Optional;

@Configuration
@Profile("!test")
public class SecurityConfig {

    private final Optional<DevAuthFilter> devAuthFilter;
    private final Optional<JwtAuthenticationFilter> jwtAuthenticationFilter;

    public SecurityConfig(Optional<DevAuthFilter> devAuthFilter,
                          Optional<JwtAuthenticationFilter> jwtAuthenticationFilter) {
        this.devAuthFilter = devAuthFilter;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        http.csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/api/v1/auth/kakao", "/api/v1/auth/reissue").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/addresses/search").permitAll()
                .requestMatchers("/api/v1/auth/seller-info").hasRole("SELLER")
                .requestMatchers("/api/v1/buyer/**").hasRole("BUYER")
                .requestMatchers("/api/v1/seller/**").hasRole("SELLER")
                .anyRequest().authenticated())
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write(
                            mapper.writeValueAsString(ApiResponse.error("UNAUTHORIZED", "인증이 필요합니다.")));
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write(
                            mapper.writeValueAsString(ApiResponse.error("FORBIDDEN", "접근 권한이 없습니다.")));
                }));

        devAuthFilter.ifPresent(filter ->
                http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class));

        jwtAuthenticationFilter.ifPresent(filter ->
                http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class));

        return http.build();
    }
}
