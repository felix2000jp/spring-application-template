package dev.felix2000jp.springapplicationtemplate.auth.domain.exceptions;

import org.springframework.security.core.AuthenticationException;

public class InvalidAuthenticationMethodException extends AuthenticationException {

    public InvalidAuthenticationMethodException() {
        super("Invalid authentication method");
    }

}
