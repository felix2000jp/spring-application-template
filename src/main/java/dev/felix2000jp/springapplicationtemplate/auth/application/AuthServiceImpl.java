package dev.felix2000jp.springapplicationtemplate.auth.application;

import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.CreateAppuserDto;
import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.UpdatePasswordDto;
import dev.felix2000jp.springapplicationtemplate.auth.domain.Appuser;
import dev.felix2000jp.springapplicationtemplate.auth.domain.AppuserRepository;
import dev.felix2000jp.springapplicationtemplate.auth.domain.exceptions.AppuserAlreadyExistsException;
import dev.felix2000jp.springapplicationtemplate.auth.domain.exceptions.AppuserNotFoundException;
import dev.felix2000jp.springapplicationtemplate.shared.SecurityService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
class AuthServiceImpl implements AuthService {

    private final AppuserRepository appuserRepository;
    private final SecurityService securityService;

    AuthServiceImpl(AppuserRepository appuserRepository, SecurityService securityService) {
        this.appuserRepository = appuserRepository;
        this.securityService = securityService;
    }

    @Override
    public Appuser loadUserByUsername(String username) {
        return appuserRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }

    @Override
    public String generateToken() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userDetails = (Appuser) authentication.getPrincipal();

        return securityService.generateToken(
                userDetails.getUsername(),
                userDetails.getId().toString(),
                String.join(" ", userDetails.getAuthoritiesScopes())
        );
    }

    @Override
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
    }

    @Override
    public void updatePassword(UpdatePasswordDto updatePasswordDto) {
        var user = securityService.getUser();

        var appuserToUpdate = appuserRepository
                .findById(user.id())
                .orElseThrow(AppuserNotFoundException::new);

        appuserToUpdate.setPassword(securityService.generateEncodedPassword(updatePasswordDto.password()));
        appuserRepository.save(appuserToUpdate);
    }

}
