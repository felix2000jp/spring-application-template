package dev.felix2000jp.springapplicationtemplate.appusers.application;


import dev.felix2000jp.springapplicationtemplate.appusers.domain.AppuserRepository;
import dev.felix2000jp.springapplicationtemplate.shared.SecurityService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
class AppuserServiceImpl implements AppuserService, UserDetailsService {

    private final AppuserRepository appuserRepository;
    private final AppuserMapper appuserMapper;
    private final SecurityService securityService;

    AppuserServiceImpl(
            AppuserRepository appuserRepository,
            AppuserMapper appuserMapper,
            SecurityService securityService
    ) {
        this.appuserRepository = appuserRepository;
        this.appuserMapper = appuserMapper;
        this.securityService = securityService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var appuser = appuserRepository.getByUsername(username);

        if (appuser == null) {
            throw new UsernameNotFoundException(username);
        }

        return appuser;
    }

}
