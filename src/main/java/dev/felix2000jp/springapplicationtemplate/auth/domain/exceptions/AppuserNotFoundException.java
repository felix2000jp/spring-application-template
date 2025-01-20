package dev.felix2000jp.springapplicationtemplate.auth.domain.exceptions;

public class AppuserNotFoundException extends RuntimeException {

    public AppuserNotFoundException() {
        super("User could not be found");
    }

}
