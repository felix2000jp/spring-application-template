package dev.felix2000jp.springapplicationtemplate.auth.application;

import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.CreateAppuserDto;
import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.UpdatePasswordDto;
import dev.felix2000jp.springapplicationtemplate.auth.domain.Appuser;
import dev.felix2000jp.springapplicationtemplate.auth.domain.AppuserRepository;
import dev.felix2000jp.springapplicationtemplate.auth.domain.exceptions.AppuserAlreadyExistsException;
import dev.felix2000jp.springapplicationtemplate.auth.domain.exceptions.AppuserNotFoundException;
import dev.felix2000jp.springapplicationtemplate.shared.SecurityService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AppuserRepository appuserRepository;
    @Mock
    private SecurityService securityService;
    @InjectMocks
    private AuthServiceImpl authService;

    @Captor
    private ArgumentCaptor<Appuser> appuserCaptor;

    @Test
    void should_load_appuser_given_username_when_appuser_exists() {
        var appuser = new Appuser("username", "password");

        when(appuserRepository.findByUsername(appuser.getUsername())).thenReturn(appuser);

        var actual = authService.loadUserByUsername(appuser.getUsername());

        assertEquals(actual, appuser);
    }

    @Test
    void should_fail_to_load_appuser_given_username_when_appuser_does_not_exists() {
        when(appuserRepository.findByUsername("username")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> authService.loadUserByUsername("username"));
    }

    @Test
    void should_generate_token_successfully_when_principal_is_of_type_appuser() {
        var appuser = new Appuser("username", "password");
        var authentication = mock(Authentication.class);
        var securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.getPrincipal()).thenReturn(appuser);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(securityService.generateToken(appuser.getUsername(), appuser.getId().toString(), "")).thenReturn("some-generated-token");

        var actual = authService.generateToken();

        assertEquals("some-generated-token", actual);
    }

    @Test
    void should_fail_to_generate_token_successfully_when_principal_is_not_of_type_appuser() {
        var someObject = new Object();
        var authentication = mock(Authentication.class);
        var securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.getPrincipal()).thenReturn(someObject);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        assertThrows(ClassCastException.class, () -> authService.generateToken());
    }

    @Test
    void should_create_appuser_when_username_is_unique() {
        var createAppuserDto = new CreateAppuserDto("username", "password");

        when(appuserRepository.existsByUsername(createAppuserDto.username())).thenReturn(false);
        when(securityService.generateEncodedPassword(createAppuserDto.password())).thenReturn("encoded-password");

        authService.createAppuser(createAppuserDto);

        verify(appuserRepository).save(appuserCaptor.capture());
        assertEquals(createAppuserDto.username(), appuserCaptor.getValue().getUsername());
        assertEquals("encoded-password", appuserCaptor.getValue().getPassword());

    }

    @Test
    void should_fail_to_create_appuser_when_username_already_exists() {
        var createAppuserDto = new CreateAppuserDto("username", "password");

        when(appuserRepository.existsByUsername(createAppuserDto.username())).thenReturn(true);

        assertThrows(AppuserAlreadyExistsException.class, () -> authService.createAppuser(createAppuserDto));
    }

    @Test
    void should_update_appuser_password_when_appuser_exists() {
        var updatePasswordDto = new UpdatePasswordDto("new password");
        var appuser = new Appuser("username", "password");
        var authenticatedUser = new SecurityService.User(
                appuser.getId(),
                appuser.getUsername(),
                appuser.getAuthoritiesScopes()
        );

        when(securityService.getUser()).thenReturn(authenticatedUser);
        when(appuserRepository.findById(appuser.getId())).thenReturn(appuser);
        when(securityService.generateEncodedPassword(updatePasswordDto.password())).thenReturn("encoded-password");

        authService.updatePassword(updatePasswordDto);

        verify(appuserRepository).save(appuserCaptor.capture());
        assertEquals("encoded-password", appuserCaptor.getValue().getPassword());
    }

    @Test
    void should_fail_to_update_appuser_password_when_appuser_does_not_exist() {
        var updatePasswordDto = new UpdatePasswordDto("new password");
        var appuser = new Appuser("username", "password");
        var authenticatedUser = new SecurityService.User(
                appuser.getId(),
                appuser.getUsername(),
                appuser.getAuthoritiesScopes()
        );

        when(securityService.getUser()).thenReturn(authenticatedUser);
        when(appuserRepository.findById(appuser.getId())).thenReturn(null);

        assertThrows(AppuserNotFoundException.class, () -> authService.updatePassword(updatePasswordDto));
    }

}
