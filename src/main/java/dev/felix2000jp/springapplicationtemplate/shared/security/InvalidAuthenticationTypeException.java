package dev.felix2000jp.springapplicationtemplate.shared.security;

public class InvalidAuthenticationTypeException extends RuntimeException {

    public InvalidAuthenticationTypeException() {
        super("Invalid authentication type, only JWT is allowed");
    }

}
