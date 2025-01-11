package dev.felix2000jp.springapplicationtemplate.auth.application;

import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.AppuserDTO;
import org.springframework.security.oauth2.jwt.Jwt;

public interface JwtAuthService {

    String generateToken(String subject, String idClaimValue, String scopeClaimValue);

    AppuserDTO getAppuserFromJwt(Jwt jwt);

}
