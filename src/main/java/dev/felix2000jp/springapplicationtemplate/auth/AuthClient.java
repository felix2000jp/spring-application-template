package dev.felix2000jp.springapplicationtemplate.auth;

import java.util.Set;
import java.util.UUID;

public interface AuthClient {

    enum ScopeValues {
        ADMIN,
        APPLICATION
    }

    record User(UUID id, String username, Set<String> authorities) {
    }

    User getUser();

    String generateToken(String subject, String idClaimValue, String scopeClaimValue);

    String generateEncodedPassword(String password);

}
