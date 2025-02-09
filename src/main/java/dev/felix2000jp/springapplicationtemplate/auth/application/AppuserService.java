package dev.felix2000jp.springapplicationtemplate.auth.application;

import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.AppuserDto;
import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.AppuserListDto;
import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.UpdateAppuserDto;
import dev.felix2000jp.springapplicationtemplate.auth.application.events.AppuserDeletedEvent;
import dev.felix2000jp.springapplicationtemplate.auth.domain.Appuser;
import dev.felix2000jp.springapplicationtemplate.auth.domain.AppuserRepository;
import dev.felix2000jp.springapplicationtemplate.auth.domain.exceptions.AppuserAlreadyExistsException;
import dev.felix2000jp.springapplicationtemplate.auth.domain.exceptions.AppuserNotFoundException;
import dev.felix2000jp.springapplicationtemplate.shared.security.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AppuserService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(AppuserService.class);

    private final AppuserRepository appuserRepository;
    private final AppuserMapper appuserMapper;
    private final AppuserPublisher appuserPublisher;
    private final SecurityService securityService;

    AppuserService(
            AppuserRepository appuserRepository,
            AppuserMapper appuserMapper,
            AppuserPublisher appuserPublisher,
            SecurityService securityService
    ) {
        this.appuserRepository = appuserRepository;
        this.appuserMapper = appuserMapper;
        this.appuserPublisher = appuserPublisher;
        this.securityService = securityService;
    }

    @Override
    public Appuser loadUserByUsername(String username) {
        return appuserRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }

    public AppuserListDto getAppusers(int pageNumber) {
        var appusers = appuserRepository.findAll(pageNumber);
        return appuserMapper.toDto(appusers);
    }

    public AppuserDto getAppuserForCurrentUser() {
        var user = securityService.getUser();

        var appuser = appuserRepository
                .findById(user.id())
                .orElseThrow(AppuserNotFoundException::new);

        return appuserMapper.toDto(appuser);
    }

    public void updateAppuser(UpdateAppuserDto updateAppuserDto) {
        var user = securityService.getUser();

        var appuser = appuserRepository
                .findById(user.id())
                .orElseThrow(AppuserNotFoundException::new);

        var isUsernameNew = !updateAppuserDto.username().equals(appuser.getUsername());
        var doesUsernameExist = appuserRepository.existsByUsername(updateAppuserDto.username());

        if (isUsernameNew && doesUsernameExist) {
            throw new AppuserAlreadyExistsException();
        }

        appuser.setUsername(updateAppuserDto.username());
        appuser.setPassword(securityService.generateEncodedPassword(updateAppuserDto.password()));
        appuserRepository.save(appuser);
        log.info("Appuser with id {} updated", appuser.getId());
    }

    @Transactional
    public void deleteAppuser() {
        var user = securityService.getUser();

        var appuser = appuserRepository
                .findById(user.id())
                .orElseThrow(AppuserNotFoundException::new);

        appuserRepository.deleteById(appuser.getId());
        log.info("Appuser with id {} deleted", appuser.getId());

        var appuserDeletedEvent = new AppuserDeletedEvent(appuser.getId());
        appuserPublisher.publish(appuserDeletedEvent);
        log.info("Published AppuserDeletedEvent with appuserId {}", appuser.getId());
    }

}
