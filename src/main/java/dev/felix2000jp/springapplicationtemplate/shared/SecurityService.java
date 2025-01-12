package dev.felix2000jp.springapplicationtemplate.shared;

import java.util.Set;
import java.util.UUID;

public interface SecurityService {

    enum Scope {
        ADMIN,
        APPLICATION;

        public String toAuthority() {
            return "SCOPE_" + name();
        }
    }

    record User(UUID id, String username, Set<String> scopes) {

        public boolean hasScopeAdmin() {
            return scopes.contains(Scope.ADMIN.name());
        }

        public boolean hasScopeApplication() {
            return scopes.contains(Scope.APPLICATION.name());
        }

    }

    User getUser();

    String generateToken(String subject, String idClaimValue, String scopeClaimValue);

    String generateEncodedPassword(String password);

}
