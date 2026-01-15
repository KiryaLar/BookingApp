package ru.larkin.bookingservice.security;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Configuration
public class JwtEncoderConfig {

    @Bean
    public SecretKey jwtSecretKey(@Value("${security.jwt.secret}") String secretBase64) {
        byte[] secret = Base64.getDecoder().decode(secretBase64);

        if (secret.length < 32) {
            throw new IllegalArgumentException("JWT secret too short for HS256. Need >= 32 bytes before base64.");
        }

        return new SecretKeySpec(secret, "HmacSHA256");
    }

    @Bean
    public JwtEncoder jwtEncoder(SecretKey jwtSecretKey) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(jwtSecretKey));
    }
}
