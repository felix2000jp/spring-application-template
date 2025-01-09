package dev.felix2000jp.springapplicationtemplate.shared;

public interface SecurityService {

    String generateToken(String subject, String idClaimValue, String scopeClaimValue);

    AuthenticatedUser getAuthenticatedUser();

}
