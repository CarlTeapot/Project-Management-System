package asterbit.projectmanagementsystem.security.config;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "jwt")
public record JwtConfigurationProperties(
        @NotBlank(message = "Private key path must not be blank")
        String privateKey,

        @NotBlank(message = "Public key path must not be blank")
        String publicKey,

        @Min(value = 15 * 60000, message = "Token expiration must be at least 15 minutes")
        long expirationMillis,

        @NotBlank(message = "Issuer must not be blank")
        String issuer,

        String audience
) {

    public JwtConfigurationProperties {
        if (expirationMillis > 86400000) {
            throw new IllegalArgumentException("Token expiration should not exceed 24 hours for security");
        }
    }
}
