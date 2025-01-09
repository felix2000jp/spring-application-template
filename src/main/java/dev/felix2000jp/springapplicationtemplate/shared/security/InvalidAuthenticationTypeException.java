package dev.felix2000jp.springapplicationtemplate.shared.security;

class InvalidAuthenticationTypeException extends RuntimeException {

    InvalidAuthenticationTypeException() {
        super("Invalid authentication type, only Jwt is allowed");
    }

}
