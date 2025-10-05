package asterbit.projectmanagementsystem.security.service;

import asterbit.projectmanagementsystem.management.model.enums.Role;
import asterbit.projectmanagementsystem.security.config.JwtConfigurationProperties;
import asterbit.projectmanagementsystem.security.exception.JwtValidationException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.*;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class JwtGeneratorService {

    private static final Logger log = LoggerFactory.getLogger(JwtGeneratorService.class);
    private static final String ROLES_CLAIM = "roles";

    private final Algorithm algorithm;
    private final JWTVerifier verifier;
    private final long expirationMillis;
    private final String issuer;
    private final String audience;

    public JwtGeneratorService(
            RSAPublicKey publicKey,
            RSAPrivateKey privateKey,
            JwtConfigurationProperties jwtConfigurationProperties
    ) {

        algorithm = Algorithm.RSA256(publicKey, privateKey);
        expirationMillis = jwtConfigurationProperties.expirationMillis();
        issuer = jwtConfigurationProperties.issuer();
        audience = jwtConfigurationProperties.audience();

        verifier = JWT.require(algorithm)
                .withIssuer(issuer)
                .withAudience(audience)
                .build();

        log.info("JWT service initialized with issuer: {}, expiration: {}ms", issuer, expirationMillis);
    }

    /**
     * Generates a JWT token for the given username and roles
     *
     * @param email the subject of the token
     * @param role user global role (e.g. admin, user)
     * @return signed JWT token
     */
    public String generateToken(String email, Role role) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or blank");
        }

        Instant now = Instant.now();
        Instant expiration = now.plusMillis(expirationMillis);

        String token = JWT.create()
                .withJWTId(UUID.randomUUID().toString())
                .withSubject(email)
                .withIssuer(issuer)
                .withAudience(audience)
                .withClaim(ROLES_CLAIM, role.toString())
                .withIssuedAt(now)
                .withExpiresAt(expiration)
                .sign(algorithm);

        log.debug("Generated token for user: {}, expires at: {}", email, expiration);
        return token;
    }

    /**
     * Validates and decodes a JWT token
     *
     * @param token the JWT token to validate
     * @return decoded JWT
     * @throws JwtValidationException if token is invalid
     */
    public DecodedJWT validateToken(String token) {
        if (token == null || token.isBlank()) {
            throw new JwtValidationException("Token cannot be null or blank");
        }

        try {
            DecodedJWT jwt = verifier.verify(token);
            log.debug("Successfully validated token for user: {}", jwt.getSubject());
            return jwt;
        } catch (TokenExpiredException e) {
            log.warn("Token expired: {}", e.getMessage());
            throw new JwtValidationException("Token has expired", e);
        } catch (SignatureVerificationException e) {
            log.error("Invalid token signature");
            throw new JwtValidationException("Invalid token signature", e);
        } catch (InvalidClaimException e) {
            log.error("Invalid token claim: {}", e.getMessage());
            throw new JwtValidationException("Invalid token claim: " + e.getMessage(), e);
        } catch (JWTDecodeException e) {
            log.error("Malformed token: {}", e.getMessage());
            throw new JwtValidationException("Malformed token", e);
        } catch (JWTVerificationException e) {
            log.error("Token verification failed: {}", e.getMessage());
            throw new JwtValidationException("Token verification failed", e);
        }
    }

    /**
     * Extracts username from token without full validation
     * Use only for non-security-critical operations
     */
    public String extractUsername(String token) {
        try {
            return JWT.decode(token).getSubject();
        } catch (JWTDecodeException e) {
            log.error("Failed to decode token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Extracts roles from a validated JWT
     */
    public List<String> extractRoles(DecodedJWT jwt) {
        return jwt.getClaim(ROLES_CLAIM).asList(String.class);
    }

    public String getExpirationMinutes() {
        return expirationMillis / 60000 + " minutes";
    }
}
