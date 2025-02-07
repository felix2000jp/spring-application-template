package dev.felix2000jp.springapplicationtemplate.auth.domain.exceptions;

public class AppuserNotFoundException extends RuntimeException {

    public AppuserNotFoundException() {
        super("SecurityUser could not be found");
    }

}
