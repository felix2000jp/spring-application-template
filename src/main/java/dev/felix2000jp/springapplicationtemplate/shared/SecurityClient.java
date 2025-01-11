package dev.felix2000jp.springapplicationtemplate.shared;

import java.util.Set;
import java.util.UUID;

public interface SecurityClient {

    enum Scope {
        ADMIN,
        APPLICATION;

        public String toAuthority() {
            return "SCOPE_" + name();
        }
    }

    record User(UUID id, String username, Set<String> authorities) {
    }

    User getUser();

    String generateToken(String subject, String idClaimValue, String scopeClaimValue);

    String generateEncodedPassword(String password);

}
