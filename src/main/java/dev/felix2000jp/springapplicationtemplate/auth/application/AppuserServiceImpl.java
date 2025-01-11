package dev.felix2000jp.springapplicationtemplate.auth.application;

import dev.felix2000jp.springapplicationtemplate.auth.AppuserDeletedEvent;
import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.AppuserDTO;
import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.AppuserListDTO;
import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.UpdateAppuserDTO;
import dev.felix2000jp.springapplicationtemplate.auth.domain.AppuserRepository;
import dev.felix2000jp.springapplicationtemplate.auth.domain.exceptions.AppuserAlreadyExistsException;
import dev.felix2000jp.springapplicationtemplate.auth.domain.exceptions.AppuserNotFoundException;
import dev.felix2000jp.springapplicationtemplate.shared.SecurityClient;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class AppuserServiceImpl implements AppuserService {

    private final AppuserRepository appuserRepository;
    private final AppuserMapper appuserMapper;
    private final SecurityClient securityClient;
    private final ApplicationEventPublisher events;

    AppuserServiceImpl(
            AppuserRepository appuserRepository,
            AppuserMapper appuserMapper,
            SecurityClient securityClient,
            ApplicationEventPublisher events
    ) {
        this.appuserRepository = appuserRepository;
        this.appuserMapper = appuserMapper;
        this.securityClient = securityClient;
        this.events = events;
    }

    @Override
    public AppuserListDTO getAll(int pageNumber) {
        var appusers = appuserRepository.getAll(pageNumber);
        return appuserMapper.toDTO(appusers);
    }

    @Override
    public AppuserDTO getAuthenticated() {
        var user = securityClient.getUser();

        var appuser = appuserRepository.getById(user.id());
        if (appuser == null) {
            throw new AppuserNotFoundException();
        }

        return appuserMapper.toDTO(appuser);
    }

    @Override
    public AppuserDTO updateAuthenticated(UpdateAppuserDTO updateAppuserDTO) {
        var user = securityClient.getUser();

        var appuserToUpdate = appuserRepository.getById(user.id());
        if (appuserToUpdate == null) {
            throw new AppuserNotFoundException();
        }

        var isUsernameNew = !updateAppuserDTO.username().equals(appuserToUpdate.getUsername());
        var doesUsernameExist = appuserRepository.existsByUsername(updateAppuserDTO.username());
        if (isUsernameNew && doesUsernameExist) {
            throw new AppuserAlreadyExistsException();
        }

        appuserToUpdate.setUsername(updateAppuserDTO.username());
        appuserRepository.save(appuserToUpdate);

        return appuserMapper.toDTO(appuserToUpdate);
    }

    @Override
    @Transactional
    public AppuserDTO deleteAuthenticated() {
        var user = securityClient.getUser();

        var appuserToDelete = appuserRepository.getById(user.id());
        if (appuserToDelete == null) {
            throw new AppuserNotFoundException();
        }

        appuserRepository.deleteById(appuserToDelete.getId());

        var appuserDeletedEvent = new AppuserDeletedEvent(appuserToDelete.getId());
        events.publishEvent(appuserDeletedEvent);

        return appuserMapper.toDTO(appuserToDelete);
    }

}
