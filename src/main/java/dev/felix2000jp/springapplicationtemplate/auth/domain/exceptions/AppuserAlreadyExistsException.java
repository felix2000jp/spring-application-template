package dev.felix2000jp.springapplicationtemplate.auth.domain.exceptions;

public class AppuserAlreadyExistsException extends RuntimeException {

    public AppuserAlreadyExistsException() {
        super("SecurityUser already exists");
    }

}
