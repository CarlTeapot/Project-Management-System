package asterbit.projectmanagementsystem.security.config;

import asterbit.projectmanagementsystem.security.exception.JwtValidationException;
import asterbit.projectmanagementsystem.security.service.JwtGeneratorService;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * JWT Authentication Filter that processes JWT tokens from the Authorization header.
 * This filter executes once per request and validates JWT tokens before allowing access to protected resources.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER = "Bearer ";
    private static final String ROLE = "ROLE_";

    private final JwtGeneratorService jwtService;

    public JwtAuthenticationFilter(JwtGeneratorService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = extractToken(request);

            if (token != null) {
                authenticateUser(token, request);
            }
        } catch (JwtValidationException e) {
            log.warn("JWT validation failed for request to {}: {}",
                    request.getRequestURI(), e.getMessage());
            SecurityContextHolder.clearContext();
        } catch (Exception e) {
            log.error("Unexpected error during JWT authentication for request to {}: {}",
                    request.getRequestURI(), e.getMessage(), e);
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);

        if (authHeader == null || authHeader.isBlank()) {
            return null;
        }

        if (!authHeader.startsWith(BEARER)) {
            log.debug("Authorization header does not start with Bearer prefix");
            return null;
        }

        String token = authHeader.substring(BEARER.length());

        if (token.isBlank()) {
            log.debug("Bearer token is empty");
            return null;
        }

        return token;
    }

    private void authenticateUser(String token, HttpServletRequest request) {
        DecodedJWT jwt = jwtService.validateToken(token);
        String username = jwt.getSubject();
        List<String> roles = jwtService.extractRoles(jwt);

        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(ensureRolePrefix(role)))
                .toList();

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(username, null, authorities);

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.debug("Successfully authenticated user: {} with roles: {}", username, roles);
    }

    private String ensureRolePrefix(String role) {
        if (role == null || role.isBlank()) {
            return ROLE;
        }
        return role.startsWith(ROLE) ? role : ROLE + role;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        return path.startsWith("/api/auth/");
    }
}
