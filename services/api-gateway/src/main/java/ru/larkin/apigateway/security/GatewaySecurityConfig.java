package ru.larkin.apigateway.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Flux;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.*;
import java.util.stream.Collectors;

@Configuration
@EnableWebFluxSecurity
public class GatewaySecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http,
                                                            ReactiveJwtDecoder jwtDecoder,
                                                            ReactiveJwtAuthenticationConverter jwtAuthConverter) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(ex -> ex
                        .pathMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .pathMatchers(HttpMethod.POST, "/users/register", "/users/auth").permitAll()
                        .pathMatchers("/auth/**").permitAll()
                        .pathMatchers("/admin/users/**").hasRole("ADMIN")
                        .pathMatchers("/bookings/**").hasAnyRole("ADMIN", "USER")
                        .pathMatchers(HttpMethod.GET, "/hotels/**").permitAll()
                        .pathMatchers("/hotels/**").hasRole("ADMIN")
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtDecoder(jwtDecoder)
                                .jwtAuthenticationConverter(jwtAuthConverter))
                )
                .build();
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder(@Value("${security.jwt.secret}") String secretBase64) {
        byte[] secret = Base64.getDecoder().decode(secretBase64);
        SecretKey key = new SecretKeySpec(secret, "HmacSHA256");
        return NimbusReactiveJwtDecoder
                .withSecretKey(key)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    @Bean
    public ReactiveJwtAuthenticationConverter jwtAuthenticationConverter() {
        ReactiveJwtAuthenticationConverter converter = new ReactiveJwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> Flux.fromIterable(extractRoleAuthorities(jwt)));
        return converter;
    }

    private static Collection<GrantedAuthority> extractRoleAuthorities(Jwt jwt) {
        // Поддержим и roles: ["USER"], и role: "USER"
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


