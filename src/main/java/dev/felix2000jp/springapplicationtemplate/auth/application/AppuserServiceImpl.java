package dev.felix2000jp.springapplicationtemplate.auth.application;

import dev.felix2000jp.springapplicationtemplate.auth.AppuserDeletedEvent;
import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.AppuserDto;
import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.AppuserListDto;
import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.UpdateAppuserDto;
import dev.felix2000jp.springapplicationtemplate.auth.domain.AppuserRepository;
import dev.felix2000jp.springapplicationtemplate.auth.domain.exceptions.AppuserAlreadyExistsException;
import dev.felix2000jp.springapplicationtemplate.auth.domain.exceptions.AppuserNotFoundException;
import dev.felix2000jp.springapplicationtemplate.shared.SecurityService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class AppuserServiceImpl implements AppuserService {

    private final AppuserRepository appuserRepository;
    private final AppuserMapper appuserMapper;
    private final SecurityService securityService;
    private final ApplicationEventPublisher events;

    AppuserServiceImpl(
            AppuserRepository appuserRepository,
            AppuserMapper appuserMapper,
            SecurityService securityService,
            ApplicationEventPublisher events
    ) {
        this.appuserRepository = appuserRepository;
        this.appuserMapper = appuserMapper;
        this.securityService = securityService;
        this.events = events;
    }

    @Override
    public AppuserListDto getAll(int pageNumber) {
        var appusers = appuserRepository.getAll(pageNumber);
        return appuserMapper.toDto(appusers);
    }

    @Override
    public AppuserDto getByCurrentUser() {
        var user = securityService.getUser();

        var appuser = appuserRepository.getById(user.id());
        if (appuser == null) {
            throw new AppuserNotFoundException();
        }

        return appuserMapper.toDto(appuser);
    }

    @Override
    public AppuserDto updateByCurrentUser(UpdateAppuserDto updateAppuserDTO) {
        var user = securityService.getUser();

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

        return appuserMapper.toDto(appuserToUpdate);
    }

    @Override
    @Transactional
    public AppuserDto deleteByCurrentUser() {
        var user = securityService.getUser();

        var appuserToDelete = appuserRepository.getById(user.id());
        if (appuserToDelete == null) {
            throw new AppuserNotFoundException();
        }

        appuserRepository.deleteById(appuserToDelete.getId());

        var appuserDeletedEvent = new AppuserDeletedEvent(appuserToDelete.getId());
        events.publishEvent(appuserDeletedEvent);

        return appuserMapper.toDto(appuserToDelete);
    }

}
