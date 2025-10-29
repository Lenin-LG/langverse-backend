package com.microservice.gateway.config;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import reactor.core.publisher.Mono;

public class ReactiveJwtAuthConverterAdapter implements Converter<Jwt, Mono<AbstractAuthenticationToken>> {

    private final JwtAuthConverter delegate;

    public ReactiveJwtAuthConverterAdapter(JwtAuthConverter delegate) {
        this.delegate = delegate;
    }

    @Override
    public Mono<AbstractAuthenticationToken> convert(Jwt jwt) {
        return Mono.just(delegate.convert(jwt));
    }
}