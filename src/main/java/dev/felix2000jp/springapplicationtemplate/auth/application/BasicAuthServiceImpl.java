package dev.felix2000jp.springapplicationtemplate.auth.application;

import dev.felix2000jp.springapplicationtemplate.auth.domain.Appuser;
import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.AppuserDTO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
class BasicAuthServiceImpl implements BasicAuthService {

    private final PasswordEncoder passwordEncoder;

    BasicAuthServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String generateEncodedPassword(String password) {
        return passwordEncoder.encode(password);
    }

    @Override
    public AppuserDTO getAppuserFromUserDetails(Appuser userDetails) {
        return new AppuserDTO(
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getAuthoritiesScopeValues()
        );
    }
}
