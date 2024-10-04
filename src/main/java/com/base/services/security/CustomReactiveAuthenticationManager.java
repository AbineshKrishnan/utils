package com.base.services.security;

import com.base.services.config.WebCommunication;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@Component
public class CustomReactiveAuthenticationManager implements ReactiveAuthenticationManager {

    private final WebCommunication webCommunication;

    public CustomReactiveAuthenticationManager(WebCommunication webCommunication) {

        this.webCommunication = webCommunication;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = (String) authentication.getCredentials();

//        return webCommunication.authenticate().map(
//                aBoolean ->
//                {
//                    if (Boolean.TRUE.equals(aBoolean)) {
//                        return new JwtAuthenticationToken(new ArrayList<>(), token, token);
//                    } else {
//                        throw new BadCredentialsException("Invalid credentials");
//                    }
//                });
        return Mono.just(new JwtAuthenticationToken(new ArrayList<>(), token, token));
    }

}
