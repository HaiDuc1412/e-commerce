package fpt.haidd69.ecommerce.services;

import org.springframework.security.core.Authentication;

import fpt.haidd69.ecommerce.entities.User;

/**
 * Service interface for JWT token operations.
 */
public interface TokenService {

    /**
     * Generate JWT token for a user.
     *
     * @param user the user entity
     * @param role the user's role
     * @return JWT token string
     */
    String generateToken(User user, String role);

    /**
     * Extract Authentication from JWT token.
     *
     * @param token JWT token string
     * @return Authentication object or null if invalid
     */
    Authentication getAuthenticationFromToken(String token);
}
