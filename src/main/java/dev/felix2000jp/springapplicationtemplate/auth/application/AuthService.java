package dev.felix2000jp.springapplicationtemplate.auth.application;

import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.CreateAppuserDTO;
import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.UpdatePasswordDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthService extends UserDetailsService {

    String generateToken();

    void createAppuser(CreateAppuserDTO createAppuserDTO);

    void updatePassword(UpdatePasswordDTO updatePasswordDTO);

}
