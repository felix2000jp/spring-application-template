package dev.felix2000jp.springapplicationtemplate.auth;

import java.util.Set;
import java.util.UUID;

public interface AuthClient {

    record AuthUser(UUID id, String username, Set<String> authorities) {
    }

    AuthUser getAuthUser();

    String generateToken(String subject, String idClaimValue, String scopeClaimValue);

    String generateEncodedPassword(String password);

}
