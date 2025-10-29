package com.microservice.gateway.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.http.HttpMethod;


@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http, JwtAuthConverter jwtAuthConverter,
                                                            CorsConfigurationSource corsConfig) {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfig))
                .authorizeExchange(exchange -> exchange
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .pathMatchers(
                                // Endpoints públicos del auth-service

                                "/auth/create",
                                "/auth/login",
                                "/auth/refresh",

                                // Swagger del gateway
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/swagger-resources/**",
                                "/webjars/**",

                                "/movies/v3/api-docs",
                                "/music/v3/api-docs",
                                "/speaking/v3/api-docs",
                                "/notes/v3/api-docs",
                                "/report/v3/api-docs",
                                "/auth/v3/api-docs"

                        ).permitAll()
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(new ReactiveJwtAuthConverterAdapter(jwtAuthConverter)))
                )
                .build();
    }
}