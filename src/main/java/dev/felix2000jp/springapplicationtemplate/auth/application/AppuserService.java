package dev.felix2000jp.springapplicationtemplate.auth.application;

import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.AppuserDto;
import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.AppuserListDto;
import dev.felix2000jp.springapplicationtemplate.auth.domain.AppuserRepository;
import dev.felix2000jp.springapplicationtemplate.auth.domain.exceptions.AppuserNotFoundException;
import dev.felix2000jp.springapplicationtemplate.shared.SecurityService;
import org.springframework.stereotype.Service;

@Service
public class AppuserService {

    private final AppuserRepository appuserRepository;
    private final AppuserMapper appuserMapper;
    private final SecurityService securityService;

    AppuserService(AppuserRepository appuserRepository, AppuserMapper appuserMapper, SecurityService securityService) {
        this.appuserRepository = appuserRepository;
        this.appuserMapper = appuserMapper;
        this.securityService = securityService;
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

}
