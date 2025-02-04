package dev.felix2000jp.springapplicationtemplate.auth.application;

import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.CreateAppuserDto;
import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.UpdateAppuserDto;
import dev.felix2000jp.springapplicationtemplate.auth.application.events.AppuserDeletedEvent;
import dev.felix2000jp.springapplicationtemplate.auth.domain.Appuser;
import dev.felix2000jp.springapplicationtemplate.auth.domain.AppuserRepository;
import dev.felix2000jp.springapplicationtemplate.auth.domain.exceptions.AppuserAlreadyExistsException;
import dev.felix2000jp.springapplicationtemplate.shared.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final AppuserRepository appuserRepository;
    private final SecurityService securityService;
    private final AppuserPublisher appuserPublisher;

    AuthService(
            AppuserRepository appuserRepository,
            SecurityService securityService,
            AppuserPublisher appuserPublisher
    ) {
        this.appuserRepository = appuserRepository;
        this.securityService = securityService;
        this.appuserPublisher = appuserPublisher;
    }

    @Override
    public Appuser loadUserByUsername(String username) {
        return appuserRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }

    public String generateToken() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userDetails = (Appuser) authentication.getPrincipal();

        return securityService.generateToken(
                userDetails.getUsername(),
                userDetails.getId().toString(),
                String.join(" ", userDetails.getAuthoritiesScopes())
        );
    }

    public void createAppuser(CreateAppuserDto createAppuserDto) {
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

    public void updateAppuser(UpdateAppuserDto updateAppuserDto) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userDetails = (Appuser) authentication.getPrincipal();

        var isUsernameNew = !updateAppuserDto.username().equals(userDetails.getUsername());
        var doesUsernameExist = appuserRepository.existsByUsername(updateAppuserDto.username());

        if (isUsernameNew && doesUsernameExist) {
            throw new AppuserAlreadyExistsException();
        }

        userDetails.setUsername(updateAppuserDto.username());
        userDetails.setPassword(securityService.generateEncodedPassword(updateAppuserDto.password()));
        appuserRepository.save(userDetails);
        log.info("Appuser with id {} updated", userDetails.getId());
    }

    @Transactional
    public void deleteAppuser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userDetails = (Appuser) authentication.getPrincipal();

        appuserRepository.deleteById(userDetails.getId());
        log.info("Appuser with id {} deleted", userDetails.getId());

        var appuserDeletedEvent = new AppuserDeletedEvent(userDetails.getId());
        appuserPublisher.publish(appuserDeletedEvent);
        log.info("Published AppuserDeletedEvent with appuserId {}", userDetails.getId());
    }

}
