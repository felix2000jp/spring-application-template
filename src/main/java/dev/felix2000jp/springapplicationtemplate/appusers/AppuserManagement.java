package dev.felix2000jp.springapplicationtemplate.appusers;

import dev.felix2000jp.springapplicationtemplate.appusers.internal.AppuserService;
import org.springframework.stereotype.Component;

@Component
public class AppuserManagement {

    private final AppuserService appuserService;

    AppuserManagement(AppuserService appuserService) {
        this.appuserService = appuserService;
    }

    public AuthenticatedAppuser getAuthenticatedAppuser() {
        return appuserService.getAuthenticatedAppuser();
    }

    public AuthenticatedAppuser verifyAuthenticatedAppuser() {
        return appuserService.verifyAuthenticatedAppuser();
    }

}
