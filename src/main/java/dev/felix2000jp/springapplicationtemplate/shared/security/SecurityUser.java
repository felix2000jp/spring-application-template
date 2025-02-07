package dev.felix2000jp.springapplicationtemplate.shared.security;

import org.springframework.modulith.NamedInterface;

import java.util.Set;
import java.util.UUID;

@NamedInterface
public record SecurityUser(UUID id, String username, Set<String> scopes) {

    public boolean hasScopeAdmin() {
        return scopes.contains(SecurityScope.ADMIN.name());
    }

    public boolean hasScopeApplication() {
        return scopes.contains(SecurityScope.APPLICATION.name());
    }

}
