package com.base.services.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public  class JwtAuthenticationToken extends AbstractAuthenticationToken {
        private final String token;
        private final Object principal;

        public JwtAuthenticationToken(Collection<? extends GrantedAuthority> authorities, String token, Object principal) {
            super(authorities);
            this.token = token;
            this.principal = principal;
           setAuthenticated(true);
        }

        @Override
        public Object getCredentials() {
            return token;
        }

        @Override
        public Object getPrincipal() {
            return principal;
        }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}