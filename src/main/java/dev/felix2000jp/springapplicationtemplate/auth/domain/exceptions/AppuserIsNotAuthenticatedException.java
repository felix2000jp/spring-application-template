package dev.felix2000jp.springapplicationtemplate.auth.domain.exceptions;

public class AppuserIsNotAuthenticatedException extends RuntimeException {

    public AppuserIsNotAuthenticatedException() {
        super("User is not authenticated or is using invalid authentication type");
    }

}
