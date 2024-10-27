package dev.felix2000jp.springapplicationtemplate.appusers;

import dev.felix2000jp.springapplicationtemplate.appusers.internal.AppuserService;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AppuserManagement {

    private final AppuserService appuserService;

    public AppuserManagement(AppuserService appuserService) {
        this.appuserService = appuserService;
    }

    public void verifyAppuserExistsById(UUID id) {
        appuserService.verifyExistsById(id);
    }

}
