package dev.felix2000jp.springapplicationtemplate.auth.application;

import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.CreateAppuserDto;
import dev.felix2000jp.springapplicationtemplate.auth.domain.Appuser;
import dev.felix2000jp.springapplicationtemplate.auth.domain.AppuserRepository;
import dev.felix2000jp.springapplicationtemplate.auth.domain.exceptions.AppuserAlreadyExistsException;
import dev.felix2000jp.springapplicationtemplate.shared.security.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final AppuserRepository appuserRepository;
    private final SecurityService securityService;

    AuthService(AppuserRepository appuserRepository, SecurityService securityService) {
        this.appuserRepository = appuserRepository;
        this.securityService = securityService;
    }

    public String login() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userDetails = (Appuser) authentication.getPrincipal();

        return securityService.generateToken(
                userDetails.getUsername(),
                userDetails.getId().toString(),
                String.join(" ", userDetails.getAuthoritiesScopes())
        );
    }

    public void register(CreateAppuserDto createAppuserDto) {
        var doesUsernameExist = appuserRepository.existsByUsername(createAppuserDto.username());

        if (doesUsernameExist) {
            throw new AppuserAlreadyExistsException();
        }

        var appuserToCreate = new Appuser(
                createAppuserDto.username(),
                securityService.generateEncodedPassword(createAppuserDto.password())
        );
        appuserToCreate.addScopeApplication();

        appuserRepository.save(appuserToCreate);
        log.info("Appuser with id {} created with scopes {}", appuserToCreate.getId(), appuserToCreate.getAuthoritiesScopes());
    }

}
