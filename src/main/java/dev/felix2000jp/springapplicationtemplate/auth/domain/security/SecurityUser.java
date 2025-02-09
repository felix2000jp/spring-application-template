package dev.felix2000jp.springapplicationtemplate.auth.domain.security;

import org.springframework.modulith.NamedInterface;

import java.util.Set;
import java.util.UUID;

@NamedInterface
public record SecurityUser(UUID id, String username, Set<SecurityScope> scopes) {

    public boolean hasScopeAdmin() {
        return scopes.contains(SecurityScope.ADMIN);
    }

    public boolean hasScopeApplication() {
        return scopes.contains(SecurityScope.APPLICATION);
    }

}
