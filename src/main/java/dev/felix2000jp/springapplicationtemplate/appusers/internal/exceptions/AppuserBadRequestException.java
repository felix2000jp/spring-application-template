package dev.felix2000jp.springapplicationtemplate.appusers.internal.exceptions;

public class AppuserBadRequestException extends RuntimeException {

    public AppuserBadRequestException() {
        super("User is not valid");
    }

    public AppuserBadRequestException(String message) {
        super(message);
    }

}
