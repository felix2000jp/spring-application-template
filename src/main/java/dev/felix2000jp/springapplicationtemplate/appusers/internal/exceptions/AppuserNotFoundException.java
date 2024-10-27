package dev.felix2000jp.springapplicationtemplate.appusers.internal.exceptions;

public class AppuserNotFoundException extends RuntimeException {

    public AppuserNotFoundException() {
        super("User could not be found");
    }

    public AppuserNotFoundException(String message) {
        super(message);
    }

}
