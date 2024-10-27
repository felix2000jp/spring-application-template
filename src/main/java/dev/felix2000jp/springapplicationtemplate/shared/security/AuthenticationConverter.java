package dev.felix2000jp.springapplicationtemplate.shared.security;

import dev.felix2000jp.springapplicationtemplate.shared.AuthorityValue;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

class AuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private static final String USERNAME_CLAIM_NAME = "username";
    private static final String AUTHORITY_CLAIM_NAME = "authorities";

    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt token) {
        var username = token.getClaimAsString(USERNAME_CLAIM_NAME);
        var authorities = token.getClaimAsStringList(AUTHORITY_CLAIM_NAME).stream()
                .map(AuthorityValue::valueOf)
                .map(value -> new SimpleGrantedAuthority(value.name()))
                .toList();

        return new AuthenticationToken(token, username, authorities);
    }

}
