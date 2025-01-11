package dev.felix2000jp.springapplicationtemplate.auth.application;

import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.CreateAppuserDTO;
import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.UpdatePasswordDTO;
import dev.felix2000jp.springapplicationtemplate.auth.domain.Appuser;
import dev.felix2000jp.springapplicationtemplate.auth.domain.AppuserRepository;
import dev.felix2000jp.springapplicationtemplate.auth.domain.exceptions.AppuserAlreadyExistsException;
import dev.felix2000jp.springapplicationtemplate.auth.domain.exceptions.AppuserNotFoundException;
import dev.felix2000jp.springapplicationtemplate.core.SecurityClient;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
class AuthServiceImpl implements AuthService {

    private final AppuserRepository appuserRepository;
    private final SecurityClient securityClient;

    AuthServiceImpl(AppuserRepository appuserRepository, SecurityClient securityClient) {
        this.appuserRepository = appuserRepository;
        this.securityClient = securityClient;
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

        return securityClient.generateToken(
                userDetails.getUsername(),
                userDetails.getId().toString(),
                String.join("", userDetails.getAuthoritiesScopeValues())
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
                securityClient.generateEncodedPassword(createAppuserDTO.password())
        );
        appuserToCreate.addApplicationScope();

        appuserRepository.save(appuserToCreate);
    }

    @Override
    public void updatePassword(UpdatePasswordDTO updatePasswordDTO) {
        var user = securityClient.getUser();

        var appuserToUpdate = appuserRepository.getById(user.id());
        if (appuserToUpdate == null) {
            throw new AppuserNotFoundException();
        }

        appuserToUpdate.setPassword(securityClient.generateEncodedPassword(updatePasswordDTO.password()));
        appuserRepository.save(appuserToUpdate);
    }

}
