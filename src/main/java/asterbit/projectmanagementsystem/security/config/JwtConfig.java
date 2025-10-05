package asterbit.projectmanagementsystem.security.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
public class JwtConfig {

    private static final Logger log = LoggerFactory.getLogger(JwtConfig.class);
    private static final String RSA_ALGORITHM = "RSA";

    private final JwtConfigurationProperties jwtConfigurationProperties;

    public JwtConfig(JwtConfigurationProperties jwtConfigurationProperties) {
        this.jwtConfigurationProperties = jwtConfigurationProperties;
    }

    @Bean
    public RSAPrivateKey rsaPrivateKey() {
        try {
            log.info("Loading RSA private key from environment variable");

            String privateKeyBase64 = jwtConfigurationProperties.privateKey();
            byte[] keyBytes = Base64.getDecoder().decode(privateKeyBase64.replaceAll("\\s+", ""));

            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            RSAPrivateKey privateKey = (RSAPrivateKey) KeyFactory.getInstance(RSA_ALGORITHM).generatePrivate(keySpec);

            if (privateKey == null) {
                throw new IllegalStateException("Private key could not be decoded");
            }

            log.info("RSA private key loaded successfully, key size: {} bits",
                    privateKey.getModulus().bitLength());
            return privateKey;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load RSA private key", e);
        }
    }

    @Bean
    public RSAPublicKey rsaPublicKey() {
        try {
            log.info("Loading RSA public key from environment variable");

            String publicKeyBase64 = jwtConfigurationProperties.publicKey();
            byte[] keyBytes = Base64.getDecoder().decode(publicKeyBase64.replaceAll("\\s+", ""));

            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            RSAPublicKey publicKey = (RSAPublicKey) KeyFactory.getInstance(RSA_ALGORITHM).generatePublic(keySpec);

            if (publicKey == null) {
                throw new IllegalStateException("Public key could not be decoded");
            }

            log.info("RSA public key loaded successfully, key size: {} bits",
                    publicKey.getModulus().bitLength());
            return publicKey;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load RSA public key", e);
        }
    }
}
