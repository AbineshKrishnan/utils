package com.base.services.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfiguration {

    private final JwtAuthenticationConverter jwtAuthenticationConverter;
    private final CustomReactiveAuthenticationManager authenticationManager;

    public SecurityConfiguration(JwtAuthenticationConverter jwtAuthenticationConverter, CustomReactiveAuthenticationManager authenticationManager) {
        this.jwtAuthenticationConverter = jwtAuthenticationConverter;
        this.authenticationManager = authenticationManager;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/apis/authenticate/**").permitAll()
                        .pathMatchers("/actuator/**").permitAll()// Public endpoints
                        .anyExchange().permitAll()
                )
                .addFilterAt(authenticationWebFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    @Bean
    public AuthenticationWebFilter authenticationWebFilter() {
        AuthenticationWebFilter filter = new AuthenticationWebFilter(authenticationManager);
        filter.setServerAuthenticationConverter(jwtAuthenticationConverter::convert);

        return filter;
    }


}