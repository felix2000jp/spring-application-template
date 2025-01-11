package dev.felix2000jp.springapplicationtemplate.auth.application;

import dev.felix2000jp.springapplicationtemplate.auth.AuthClient;
import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.AppuserDTO;
import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.AppuserListDTO;
import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.CreateAppuserDTO;
import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.UpdateAppuserDTO;
import dev.felix2000jp.springapplicationtemplate.auth.domain.Appuser;
import dev.felix2000jp.springapplicationtemplate.auth.domain.AppuserRepository;
import dev.felix2000jp.springapplicationtemplate.auth.domain.exceptions.AppuserNotFoundException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
class AppuserServiceImpl implements UserDetailsService, AppuserService {

    private final AppuserRepository appuserRepository;
    private final AppuserMapper appuserMapper;
    private final AuthClient authClient;

    AppuserServiceImpl(AppuserRepository appuserRepository, AppuserMapper appuserMapper, AuthClient authClient) {
        this.appuserRepository = appuserRepository;
        this.appuserMapper = appuserMapper;
        this.authClient = authClient;
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
    public AppuserListDTO getAll(int pageNumber) {
        var appusers = appuserRepository.getAll(pageNumber);
        return appuserMapper.toDTO(appusers);
    }

    @Override
    public AppuserDTO getById(UUID id) {
        var note = appuserRepository.getById(id);

        if (note == null) {
            throw new AppuserNotFoundException();
        }

        return appuserMapper.toDTO(note);
    }

    @Override
    public AppuserDTO create(CreateAppuserDTO createAppuserDTO) {
        return null;
    }

    @Override
    public AppuserDTO updateById(UUID id, UpdateAppuserDTO updateAppuserDTO) {
        return null;
    }

    @Override
    public AppuserDTO deleteById(UUID id) {
        var appuserToDelete = appuserRepository.getById(id);

        if (appuserToDelete == null) {
            throw new AppuserNotFoundException();
        }

        appuserRepository.deleteById(appuserToDelete.getId());
        return appuserMapper.toDTO(appuserToDelete);
    }

    @Override
    public String createToken() {
        var authenticatedUser = authClient.getUser();

        return authClient.generateToken(
                authenticatedUser.username(),
                authenticatedUser.id().toString(),
                String.join("", authenticatedUser.authorities())
        );
    }

}
