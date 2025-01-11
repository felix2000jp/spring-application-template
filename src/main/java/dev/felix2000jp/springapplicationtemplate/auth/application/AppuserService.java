package dev.felix2000jp.springapplicationtemplate.auth.application;

import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.AppuserDTO;

public interface AppuserService {

    AppuserDTO getAuthenticatedAppuser();
    
    String createToken();

}
