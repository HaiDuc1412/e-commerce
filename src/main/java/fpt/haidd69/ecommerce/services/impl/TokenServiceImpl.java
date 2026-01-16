package fpt.haidd69.ecommerce.services.impl;

import java.util.Date;
import java.util.Set;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import fpt.haidd69.ecommerce.entities.User;
import fpt.haidd69.ecommerce.services.TokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of TokenService for JWT token generation and validation.
 */
@Slf4j
@Service
public class TokenServiceImpl implements TokenService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private Long jwtExpirationInMs;

    @Override
    public String generateToken(User user, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        SecretKey key = getSigningKey();

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("userId", user.getId())
                .claim("email", user.getEmail())
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    @Override
    public Authentication getAuthenticationFromToken(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            // Use email as the principal
            String email = claims.getSubject();
            String role = claims.get("role", String.class);

            Set<GrantedAuthority> authorities = Set.of(
                new SimpleGrantedAuthority("ROLE_" + role)
            );

            org.springframework.security.core.userdetails.User principal = 
                new org.springframework.security.core.userdetails.User(email, "", authorities);

            return new UsernamePasswordAuthenticationToken(principal, token, authorities);

        } catch (ExpiredJwtException e) {
            log.warn("JWT token is expired: {}", e.getMessage());
            return null;
        } catch (JwtException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            return null;
        } catch (Exception e) {
            log.error("Cannot parse JWT token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Get the signing key for JWT operations.
     * 
     * @return SecretKey for signing/verifying JWT
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }
}
