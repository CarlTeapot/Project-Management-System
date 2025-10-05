package asterbit.projectmanagementsystem.security.exception;

/**
 * Custom exception for JWT validation failures
 */
public class JwtValidationException extends RuntimeException {
    public JwtValidationException(String message) {
        super(message);
    }

    public JwtValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}