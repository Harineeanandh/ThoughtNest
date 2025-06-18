package com.thoughtNest.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.function.Function;

/**
 * Utility class for generating, validating, and extracting data from JWT tokens.
 */
@Component
public class JwtUtil {

    // The secret key used to sign and verify JWT tokens
    @Value("${jwt.secret}")
    private String secret;

    // Token validity period: 24 hours (in milliseconds)
    private static final long JWT_EXPIRATION_MS = 24 * 60 * 60 * 1000;

    /**
     * Generates a JWT token using the user's email as the subject.
     *
     * @param email the user's email (used as token subject)
     * @return a signed JWT token string
     */
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email) // subject is the email
                .setIssuedAt(new Date()) // token creation time
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION_MS)) // expiry time
                .signWith(SignatureAlgorithm.HS256, secret) // signing algorithm and secret
                .compact(); // generate the token string
    }

    /**
     * Validates a JWT token by checking:
     * 1. If the subject matches the given email
     * 2. If the token is not expired
     *
     * @param token the JWT token to validate
     * @param email the expected email from token
     * @return true if valid, false otherwise
     */
    public Boolean validateToken(String token, String email) {
        final String tokenEmail = extractUsername(token); // subject == email
        return (tokenEmail.equals(email) && !isTokenExpired(token));
    }

    /**
     * Extracts the subject (email) from the token.
     *
     * @param token the JWT token
     * @return the subject (email) stored in the token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the expiration date from the token.
     *
     * @param token the JWT token
     * @return expiration date
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Generic method to extract any claim from the token using a resolver function.
     *
     * @param token the JWT token
     * @param claimsResolver a function that extracts a specific claim from the Claims object
     * @return the resolved claim value
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parser()
                .setSigningKey(secret) // use the same secret used to sign the token
                .parseClaimsJws(token) // parse the token
                .getBody(); // get token claims (payload)
        return claimsResolver.apply(claims);
    }

    /**
     * Checks if the token has expired.
     *
     * @param token the JWT token
     * @return true if expired, false if still valid
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}
