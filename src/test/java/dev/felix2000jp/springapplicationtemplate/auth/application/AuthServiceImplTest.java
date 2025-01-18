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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
    void loadUserByUsername_given_username_of_appuser_then_return_appuser() {
        var appuser = new Appuser("username", "password");

        when(appuserRepository.findByUsername(appuser.getUsername())).thenReturn(Optional.of(appuser));

        var actual = authService.loadUserByUsername(appuser.getUsername());

        assertThat(actual).isEqualTo(appuser);
    }

    @Test
    void loadUserByUsername_given_not_found_username_then_throw_user_not_found_exception() {
        when(appuserRepository.findByUsername("username")).thenReturn(null);

        assertThatThrownBy(() -> authService.loadUserByUsername("username")).isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void generateToken_given_appuser_principal_then_generate_valid_token() {
        var appuser = new Appuser("username", "password");
        var authentication = mock(Authentication.class);
        var securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.getPrincipal()).thenReturn(appuser);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(securityService.generateToken(appuser.getUsername(), appuser.getId().toString(), "")).thenReturn("some-generated-token");

        var actual = authService.generateToken();

        assertThat(actual).isEqualTo("some-generated-token");
    }

    @Test
    void generateToken_given_principal_of_invalid_type_then_throw_class_cast_exception() {
        var someObject = new Object();
        var authentication = mock(Authentication.class);
        var securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.getPrincipal()).thenReturn(someObject);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        assertThatThrownBy(() -> authService.generateToken()).isInstanceOf(ClassCastException.class);
    }

    @Test
    void createAppuser_given_create_appuser_dto_then_create_appuser() {
        var createAppuserDto = new CreateAppuserDto("username", "password");

        when(appuserRepository.existsByUsername(createAppuserDto.username())).thenReturn(false);
        when(securityService.generateEncodedPassword(createAppuserDto.password())).thenReturn("encoded-password");

        authService.createAppuser(createAppuserDto);

        verify(appuserRepository).save(appuserCaptor.capture());

        assertThat(appuserCaptor.getValue().getUsername()).isEqualTo(createAppuserDto.username());
        assertThat(appuserCaptor.getValue().getPassword()).isEqualTo("encoded-password");
    }

    @Test
    void createAppuser_given_duplicate_username_then_throw_appuser_already_exists_exception() {
        var createAppuserDto = new CreateAppuserDto("username", "password");

        when(appuserRepository.existsByUsername(createAppuserDto.username())).thenReturn(true);

        assertThatThrownBy(() -> authService.createAppuser(createAppuserDto)).isInstanceOf(AppuserAlreadyExistsException.class);
    }

    @Test
    void updatePassword_given_update_password_dto_then_update_password() {
        var updatePasswordDto = new UpdatePasswordDto("new password");
        var appuser = new Appuser("username", "password");
        var authenticatedUser = new SecurityService.User(
                appuser.getId(),
                appuser.getUsername(),
                appuser.getAuthoritiesScopes()
        );

        when(securityService.getUser()).thenReturn(authenticatedUser);
        when(appuserRepository.findById(appuser.getId())).thenReturn(Optional.of(appuser));
        when(securityService.generateEncodedPassword(updatePasswordDto.password())).thenReturn("encoded-password");

        authService.updatePassword(updatePasswordDto);

        verify(appuserRepository).save(appuserCaptor.capture());
        assertThat(appuserCaptor.getValue().getPassword()).isEqualTo("encoded-password");
    }

    @Test
    void updatePassword_given_not_found_authenticated_user_then_throw_user_not_found_exception() {
        var updatePasswordDto = new UpdatePasswordDto("new password");
        var appuser = new Appuser("username", "password");
        var authenticatedUser = new SecurityService.User(
                appuser.getId(),
                appuser.getUsername(),
                appuser.getAuthoritiesScopes()
        );

        when(securityService.getUser()).thenReturn(authenticatedUser);
        when(appuserRepository.findById(appuser.getId())).thenReturn(null);

        assertThatThrownBy(() -> authService.updatePassword(updatePasswordDto)).isInstanceOf(AppuserNotFoundException.class);
    }

}
