package dev.felix2000jp.springapplicationtemplate.appusers.internal.exceptions;

public class AppuserConflictException extends RuntimeException {

    public AppuserConflictException() {
        super("User already exists");
    }

    public AppuserConflictException(String message) {
        super(message);
    }

}
