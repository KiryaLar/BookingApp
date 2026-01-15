package ru.larkin.bookingservice.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class BookingSecurityConfig {

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
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationConverter jwtAuthConverter)
            throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.
                        sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/actuator/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/users/register", "/users/auth").permitAll()

                        .requestMatchers("/admin/users").hasRole("ADMIN")
                        .requestMatchers("/admin/bookings").hasRole("ADMIN")

                        .requestMatchers("/bookings/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/bookings/**").hasAuthority("USER")

                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter)))
                .build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(BookingSecurityConfig::extractRoleAuthorities);
        return converter;
    }

    private static Collection<GrantedAuthority> extractRoleAuthorities(Jwt jwt) {
        Set<String> roles = new LinkedHashSet<>();

        Object rolesClaim = jwt.getClaims().get("roles");
        if (rolesClaim instanceof Collection<?> c) {
            c.forEach(r -> roles.add(String.valueOf(r)));
        } else if (rolesClaim instanceof String s) {
            roles.addAll(Arrays.asList(s.split("[,\\s]+")));
        }

        Object roleClaim = jwt.getClaims().get("role");
        if (roleClaim instanceof String s) {
            roles.add(s);
        }

        return roles.stream()
                .map(String::trim)
                .filter(v -> !v.isEmpty())
                .map(v -> v.startsWith("ROLE_") ? v.substring("ROLE_".length()) : v)
                .map(String::toUpperCase)
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                .collect(Collectors.toUnmodifiableSet());
    }
}
