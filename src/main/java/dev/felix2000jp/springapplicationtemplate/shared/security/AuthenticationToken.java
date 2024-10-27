package dev.felix2000jp.springapplicationtemplate.shared.security;

import dev.felix2000jp.springapplicationtemplate.shared.AppuserPrincipal;
import dev.felix2000jp.springapplicationtemplate.shared.AuthorityValue;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collection;
import java.util.UUID;

class AuthenticationToken extends JwtAuthenticationToken {

    AuthenticationToken(Jwt jwt, String username, Collection<? extends GrantedAuthority> authorities) {
        super(jwt, authorities, username);
    }

    @Override
    public AppuserPrincipal getPrincipal() {
        return new AppuserPrincipal(
                UUID.fromString(getToken().getSubject()),
                getName(),
                getAuthorities().stream().map(authority -> AuthorityValue.valueOf(authority.getAuthority())).toList()
        );
    }

}