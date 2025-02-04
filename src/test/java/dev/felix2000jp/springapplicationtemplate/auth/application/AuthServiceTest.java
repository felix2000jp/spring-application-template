package dev.felix2000jp.springapplicationtemplate.auth.application;

import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.CreateAppuserDto;
import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.UpdateAppuserDto;
import dev.felix2000jp.springapplicationtemplate.auth.application.events.AppuserDeletedEvent;
import dev.felix2000jp.springapplicationtemplate.auth.domain.Appuser;
import dev.felix2000jp.springapplicationtemplate.auth.domain.AppuserRepository;
import dev.felix2000jp.springapplicationtemplate.auth.domain.exceptions.AppuserAlreadyExistsException;
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
class AuthServiceTest {

    @Mock
    private AppuserRepository appuserRepository;
    @Mock
    private SecurityService securityService;
    @Mock
    private AppuserPublisher appuserPublisher;
    @InjectMocks
    private AuthService authService;

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
    void loadUserByUsername_given_not_found_username_then_throw_appuser_not_found_exception() {
        when(appuserRepository.findByUsername("username")).thenReturn(Optional.empty());

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
        when(
                securityService.generateToken(eq(appuser.getUsername()), any(), eq(""))
        ).thenReturn("some-generated-token");

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
    void createAppuser_given_dto_then_create_appuser() {
        var createAppuserDto = new CreateAppuserDto("username", "password");

        when(appuserRepository.existsByUsername(createAppuserDto.username())).thenReturn(false);
        when(securityService.generateEncodedPassword(createAppuserDto.password())).thenReturn("encoded-password");

        authService.createAppuser(createAppuserDto);

        verify(appuserRepository).save(appuserCaptor.capture());

        assertThat(appuserCaptor.getValue().getUsername()).isEqualTo(createAppuserDto.username());
        assertThat(appuserCaptor.getValue().getPassword()).isEqualTo("encoded-password");
    }

    @Test
    void createAppuser_given_dto_with_duplicate_username_then_throw_appuser_already_exists_exception() {
        var createAppuserDto = new CreateAppuserDto("username", "password");

        when(appuserRepository.existsByUsername(createAppuserDto.username())).thenReturn(true);

        assertThatThrownBy(() -> authService.createAppuser(createAppuserDto)).isInstanceOf(AppuserAlreadyExistsException.class);
    }

    @Test
    void updateAppuser_given_dto_then_update_username_and_password() {
        var updateAppuserDto = new UpdateAppuserDto("new username", "new password");
        var appuser = new Appuser("username", "password");
        var authentication = mock(Authentication.class);
        var securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.getPrincipal()).thenReturn(appuser);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(appuserRepository.existsByUsername(updateAppuserDto.username())).thenReturn(false);
        when(securityService.generateEncodedPassword(updateAppuserDto.password())).thenReturn("encoded-password");

        authService.updateAppuser(updateAppuserDto);

        verify(appuserRepository).save(appuserCaptor.capture());
        assertThat(appuserCaptor.getValue().getUsername()).isEqualTo("new username");
        assertThat(appuserCaptor.getValue().getPassword()).isEqualTo("encoded-password");
    }

    @Test
    void updateAppuser_given_dto_with_duplicate_username_then_throw_appuser_already_exists_exception() {
        var updateAppuserDto = new UpdateAppuserDto("duplicate username", "new password");
        var appuser = new Appuser("username", "password");
        var authentication = mock(Authentication.class);
        var securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.getPrincipal()).thenReturn(appuser);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(appuserRepository.existsByUsername(updateAppuserDto.username())).thenReturn(true);

        assertThatThrownBy(() -> authService.updateAppuser(updateAppuserDto)).isInstanceOf(AppuserAlreadyExistsException.class);
    }

    @Test
    void deleteAppuser_given_authenticated_user_then_delete_appuser() {
        var appuser = new Appuser("username", "password");
        var authentication = mock(Authentication.class);
        var securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.getPrincipal()).thenReturn(appuser);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        authService.deleteAppuser();

        verify(appuserRepository).deleteById(appuser.getId());
        verify(appuserPublisher).publish(new AppuserDeletedEvent(appuser.getId()));
    }

}
