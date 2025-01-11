package dev.felix2000jp.springapplicationtemplate.auth.application;

import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.AppuserDTO;
import dev.felix2000jp.springapplicationtemplate.auth.domain.Appuser;
import dev.felix2000jp.springapplicationtemplate.auth.domain.AppuserRepository;
import dev.felix2000jp.springapplicationtemplate.auth.domain.exceptions.AppuserIsNotAuthenticatedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
class AppuserServiceImpl implements UserDetailsService, AppuserService {

    private final AppuserRepository appuserRepository;
    private final AppuserMapper appuserMapper;
    private final BasicAuthService basicAuthService;
    private final JwtAuthService jwtAuthService;

    AppuserServiceImpl(
            AppuserRepository appuserRepository,
            AppuserMapper appuserMapper,
            BasicAuthService basicAuthService,
            JwtAuthService jwtAuthService
    ) {
        this.appuserRepository = appuserRepository;
        this.appuserMapper = appuserMapper;
        this.basicAuthService = basicAuthService;
        this.jwtAuthService = jwtAuthService;
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
    public AppuserDTO getAuthenticatedAppuser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new AppuserIsNotAuthenticatedException();
        }

        if (authentication.getPrincipal() instanceof Appuser appuser) {
            return basicAuthService.getAppuserFromUserDetails(appuser);
        }

        if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwtAuthService.getAppuserFromJwt(jwt);
        }

        throw new AppuserIsNotAuthenticatedException();
    }

    @Override
    public String createToken() {
        var authenticatedUser = getAuthenticatedAppuser();

        return jwtAuthService.generateToken(
                authenticatedUser.username(),
                authenticatedUser.id().toString(),
                String.join("", authenticatedUser.authorities())
        );
    }

}
