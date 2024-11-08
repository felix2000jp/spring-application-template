package dev.felix2000jp.springapplicationtemplate.appusers;

import dev.felix2000jp.springapplicationtemplate.appusers.internal.AppuserService;
import dev.felix2000jp.springapplicationtemplate.appusers.internal.dtos.AuthenticatedAppuserDTO;
import org.springframework.stereotype.Component;

@Component
public class AppuserManagement {

    private final AppuserService appuserService;

    AppuserManagement(AppuserService appuserService) {
        this.appuserService = appuserService;
    }

    public AuthenticatedAppuserDTO getAuthenticatedAppuserDTO() {
        return appuserService.getAuthenticatedAppuserDTO();
    }

    public AuthenticatedAppuserDTO verifyAuthenticatedAppuserDTO() {
        return appuserService.verifyAuthenticatedAppuserDTO();
    }

}
