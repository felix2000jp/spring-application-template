package dev.felix2000jp.springapplicationtemplate.auth.application;

import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.CreateAppuserDTO;
import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.UpdatePasswordDTO;
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
    public Appuser loadUserByUsername(String username) throws UsernameNotFoundException {
        var appuser = appuserRepository.getByUsername(username);

        if (appuser == null) {
            throw new UsernameNotFoundException(username);
        }

        return appuser;
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
    public void createAppuser(CreateAppuserDTO createAppuserDTO) {
        var doesUsernameExist = appuserRepository.existsByUsername(createAppuserDTO.username());
        if (doesUsernameExist) {
            throw new AppuserAlreadyExistsException();
        }

        var appuserToCreate = new Appuser(
                createAppuserDTO.username(),
                securityService.generateEncodedPassword(createAppuserDTO.password())
        );
        appuserToCreate.addApplicationScope();

        appuserRepository.save(appuserToCreate);
    }

    @Override
    public void updatePassword(UpdatePasswordDTO updatePasswordDTO) {
        var user = securityService.getUser();

        var appuserToUpdate = appuserRepository.getById(user.id());
        if (appuserToUpdate == null) {
            throw new AppuserNotFoundException();
        }

        appuserToUpdate.setPassword(securityService.generateEncodedPassword(updatePasswordDTO.password()));
        appuserRepository.save(appuserToUpdate);
    }

}
