package ru.larkin.hotelmanagementservice.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.Collection;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public JwtDecoder jwtDecoder(@Value("${security.jwt.secret}") String secretBase64) {
        byte[] secret = java.util.Base64.getDecoder().decode(secretBase64);
        javax.crypto.SecretKey key = new javax.crypto.spec.SecretKeySpec(secret, "HmacSHA256");
        return org.springframework.security.oauth2.jwt.NimbusJwtDecoder
                .withSecretKey(key)
                .macAlgorithm(org.springframework.security.oauth2.jose.jws.MacAlgorithm.HS256)
                .build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/users/register", "/users/auth").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/hotels/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/hotels/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/hotels/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/hotels/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/hotels/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/rooms/**").permitAll()
                        .requestMatchers("/rooms/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        var scopes = new JwtGrantedAuthoritiesConverter(); // читает scope/scp
        scopes.setAuthorityPrefix("SCOPE_");

        return new JwtAuthenticationConverter() {{
            setJwtGrantedAuthoritiesConverter(jwt -> {
                Collection<GrantedAuthority> authorities = new ArrayList<>(scopes.convert(jwt));

                Object rolesClaim = jwt.getClaim("roles");
                if (rolesClaim instanceof Collection<?> roles) {
                    for (Object r : roles) {
                        authorities.add(new SimpleGrantedAuthority("ROLE_" + r));
                    }
                }
                return authorities;
            });
        }};
    }
}

