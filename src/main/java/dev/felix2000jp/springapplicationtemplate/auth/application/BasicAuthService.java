package dev.felix2000jp.springapplicationtemplate.auth.application;


import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.AppuserDTO;
import dev.felix2000jp.springapplicationtemplate.auth.domain.Appuser;

public interface BasicAuthService {

    String generateEncodedPassword(String password);

    AppuserDTO getAppuserFromUserDetails(Appuser userDetails);

}
