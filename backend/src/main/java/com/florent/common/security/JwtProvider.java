package com.florent.common.security;

import com.florent.common.exception.BusinessException;
import com.florent.common.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.util.Date;

@Component
public class JwtProvider {

    private final SecretKey secretKey;
    private final long accessTokenValidityMs;
    private final long refreshTokenValidityMs;
    private final Clock clock;

    public JwtProvider(JwtProperties properties, Clock clock) {
        this.secretKey = Keys.hmacShaKeyFor(properties.secretKey().getBytes(StandardCharsets.UTF_8));
        this.accessTokenValidityMs = properties.accessTokenValidityMs();
        this.refreshTokenValidityMs = properties.refreshTokenValidityMs();
        this.clock = clock;
    }

    public String generateAccessToken(Long userId, String role, Long buyerId, Long sellerId) {
        Date now = Date.from(clock.instant());
        Date expiry = new Date(now.getTime() + accessTokenValidityMs);

        var builder = Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiry);

        if (buyerId != null) {
            builder.claim("buyerId", buyerId);
        }
        if (sellerId != null) {
            builder.claim("sellerId", sellerId);
        }

        return builder.signWith(secretKey).compact();
    }

    public String generateRefreshToken(Long userId) {
        Date now = Date.from(clock.instant());
        Date expiry = new Date(now.getTime() + refreshTokenValidityMs);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();
    }

    public Claims validateAndExtractClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .clock(() -> Date.from(clock.instant()))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new BusinessException(ErrorCode.TOKEN_EXPIRED);
        } catch (JwtException e) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
    }
}
