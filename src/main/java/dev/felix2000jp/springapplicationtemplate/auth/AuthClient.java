package dev.felix2000jp.springapplicationtemplate.auth;

import dev.felix2000jp.springapplicationtemplate.auth.application.BasicAuthService;
import dev.felix2000jp.springapplicationtemplate.auth.application.JwtAuthService;
import dev.felix2000jp.springapplicationtemplate.auth.domain.Appuser;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Component
public class AuthClient {

    private final BasicAuthService basicAuthService;
    private final JwtAuthService jwtAuthService;

    AuthClient(BasicAuthService basicAuthService, JwtAuthService jwtAuthService) {
        this.basicAuthService = basicAuthService;
        this.jwtAuthService = jwtAuthService;
    }

    public record AuthUser(UUID id, String username, Set<String> authorities) {
    }


    public String generateToken(String subject, String idClaimValue, String scopeClaimValue) {
        return jwtAuthService.generateToken(subject, idClaimValue, scopeClaimValue);
    }


    public String generateEncodedPassword(String password) {
        return basicAuthService.generateEncodedPassword(password);
    }


    public AuthUser getAuthenticatedUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return null;
        }

        if (authentication.getPrincipal() instanceof Appuser appuser) {
            var appuserDTO = basicAuthService.getAppuserFromUserDetails(appuser);
            return new AuthUser(appuserDTO.id(), appuserDTO.username(), appuserDTO.authorities());
        }

        if (authentication.getPrincipal() instanceof Jwt jwt) {
            var appuserDTO = jwtAuthService.getAppuserFromJwt(jwt);
            return new AuthUser(appuserDTO.id(), appuserDTO.username(), appuserDTO.authorities());

        }

        return null;
    }

}
