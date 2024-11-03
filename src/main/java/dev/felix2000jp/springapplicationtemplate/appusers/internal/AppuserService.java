package dev.felix2000jp.springapplicationtemplate.appusers.internal;

import dev.felix2000jp.springapplicationtemplate.appusers.AppuserDeletedEvent;
import dev.felix2000jp.springapplicationtemplate.appusers.internal.dtos.AppuserDto;
import dev.felix2000jp.springapplicationtemplate.appusers.internal.dtos.CreateAppuserDto;
import dev.felix2000jp.springapplicationtemplate.appusers.internal.dtos.UpdateAppuserDto;
import dev.felix2000jp.springapplicationtemplate.appusers.internal.exceptions.AppuserConflictException;
import dev.felix2000jp.springapplicationtemplate.appusers.internal.exceptions.AppuserNotFoundException;
import dev.felix2000jp.springapplicationtemplate.shared.AuthorityValue;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AppuserService implements UserDetailsService {

    private final AppuserMapper appuserMapper;
    private final AppuserRepository appuserRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher events;

    public AppuserService(
            AppuserMapper appuserMapper,
            AppuserRepository appuserRepository,
            PasswordEncoder passwordEncoder,
            ApplicationEventPublisher events
    ) {
        this.appuserMapper = appuserMapper;
        this.appuserRepository = appuserRepository;
        this.passwordEncoder = passwordEncoder;
        this.events = events;
    }

    AppuserDto find(Appuser principal) {
        var appuser = appuserRepository
                .findById(principal.getId())
                .orElseThrow(AppuserNotFoundException::new);

        return appuserMapper.toDto(appuser);
    }

    AppuserDto create(CreateAppuserDto createAppuserDto) {
        var doesUsernameExist = appuserRepository.existsByUsername(createAppuserDto.username());

        if (doesUsernameExist) {
            throw new AppuserConflictException();
        }

        var appuserCreated = new Appuser(
                createAppuserDto.username(),
                passwordEncoder.encode(createAppuserDto.password()),
                AuthorityValue.APPLICATION
        );

        var appuserSaved = appuserRepository.save(appuserCreated);
        return appuserMapper.toDto(appuserSaved);
    }

    AppuserDto update(Appuser principal, UpdateAppuserDto updateAppuserDto) {
        var appuserToUpdate = appuserRepository
                .findById(principal.getId())
                .orElseThrow(AppuserNotFoundException::new);

        var newUsername = updateAppuserDto.username();
        var oldUsername = appuserToUpdate.getUsername();

        var isUsernameNew = !newUsername.equals(oldUsername);
        var doesUsernameExist = appuserRepository.existsByUsername(newUsername);

        if (isUsernameNew && doesUsernameExist) {
            throw new AppuserConflictException();
        }

        appuserToUpdate.updateCredentials(
                updateAppuserDto.username(),
                passwordEncoder.encode(updateAppuserDto.password())
        );
        var appuserSaved = appuserRepository.save(appuserToUpdate);
        return appuserMapper.toDto(appuserSaved);
    }

    @Transactional
    AppuserDto delete(Appuser principal) {
        var userToDelete = appuserRepository
                .findById(principal.getId())
                .orElseThrow(AppuserNotFoundException::new);

        appuserRepository.delete(userToDelete);

        var appuserDeletedEvent = new AppuserDeletedEvent(userToDelete.getId());
        events.publishEvent(appuserDeletedEvent);

        return appuserMapper.toDto(userToDelete);
    }

    public void verifyExistsById(UUID id) {
        var doesAppuserExist = appuserRepository.existsById(id);

        if (!doesAppuserExist) {
            throw new AppuserNotFoundException();
        }
    }

    @Override
    public Appuser loadUserByUsername(String username) {
        return appuserRepository
                .findByUsername(username)
                .orElseThrow(AppuserNotFoundException::new);
    }

}
