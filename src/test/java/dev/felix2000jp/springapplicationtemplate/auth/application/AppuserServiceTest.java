package dev.felix2000jp.springapplicationtemplate.auth.application;

import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.CreateAppuserDto;
import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.UpdateAppuserDto;
import dev.felix2000jp.springapplicationtemplate.auth.application.events.AppuserDeletedEvent;
import dev.felix2000jp.springapplicationtemplate.auth.domain.Appuser;
import dev.felix2000jp.springapplicationtemplate.auth.domain.AppuserRepository;
import dev.felix2000jp.springapplicationtemplate.auth.domain.exceptions.AppuserAlreadyExistsException;
import dev.felix2000jp.springapplicationtemplate.auth.domain.exceptions.AppuserNotFoundException;
import dev.felix2000jp.springapplicationtemplate.auth.domain.SecurityUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppuserServiceTest {

    @Mock
    private AppuserRepository appuserRepository;
    @Spy
    private AppuserMapper appuserMapper;
    @Mock
    private AppuserPublisher appuserPublisher;
    @Mock
    private SecurityService securityService;
    @InjectMocks
    private AppuserService appuserService;

    @Captor
    private ArgumentCaptor<Appuser> appuserCaptor;

    @Test
    void register_given_dto_then_create_user() {
        var createAppuserDto = new CreateAppuserDto("username", "password");

        when(appuserRepository.existsByUsername(createAppuserDto.username())).thenReturn(false);
        when(securityService.generateEncodedPassword(createAppuserDto.password())).thenReturn("encoded-password");

        appuserService.register(createAppuserDto);

        verify(appuserRepository).save(appuserCaptor.capture());

        assertThat(appuserCaptor.getValue().getUsername()).isEqualTo(createAppuserDto.username());
        assertThat(appuserCaptor.getValue().getPassword()).isEqualTo("encoded-password");
    }

    @Test
    void register_already_exists_exception() {
        var createAppuserDto = new CreateAppuserDto("username", "password");

        when(appuserRepository.existsByUsername(createAppuserDto.username())).thenReturn(true);

        assertThatThrownBy(() -> appuserService.register(createAppuserDto)).isInstanceOf(AppuserAlreadyExistsException.class);
    }

    @Test
    void getAppusers_given_page_with_data_then_return_list_of_appusers() {
        var appuser = new Appuser("username", "password");

        when(appuserRepository.findAll(0)).thenReturn(List.of(appuser));

        var actual = appuserService.getAppusers(0);
        var actualAppuser = actual.appusers().getFirst();

        assertThat(actual.appusers()).hasSize(1);
        assertThat(actualAppuser.id()).isEqualTo(appuser.getId());
        assertThat(actualAppuser.username()).isEqualTo(appuser.getUsername());
        assertThat(actualAppuser.scopes()).isEqualTo(appuser.getAuthoritiesScopes());
    }

    @Test
    void getAppusers_given_page_with_no_data_then_return_empty_list_of_appusers() {
        when(appuserRepository.findAll(0)).thenReturn(List.of());

        var actual = appuserService.getAppusers(0);

        assertThat(actual.appusers()).isEmpty();
    }

    @Test
    void getAppuserForCurrentUser_given_authenticated_user_then_return_appuser_equivalent() {
        var appuser = new Appuser("username", "password");
        var authenticatedUser = appuser.toSecurityUser();

        when(securityService.getUser()).thenReturn(authenticatedUser);
        when(appuserRepository.findById(authenticatedUser.id())).thenReturn(Optional.of(appuser));

        var actual = appuserService.getAppuserForCurrentUser();

        assertThat(actual.id()).isEqualTo(appuser.getId());
        assertThat(actual.username()).isEqualTo(appuser.getUsername());
        assertThat(actual.scopes()).isEqualTo(appuser.getAuthoritiesScopes());
    }

    @Test
    void getAppuserForCurrentUser_given_not_found_authenticated_user_then_throw_appuser_not_found_exception() {
        var authenticatedUser = new SecurityUser(UUID.randomUUID(), "username", Set.of());

        when(securityService.getUser()).thenReturn(authenticatedUser);
        when(appuserRepository.findById(authenticatedUser.id())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appuserService.getAppuserForCurrentUser()).isInstanceOf(AppuserNotFoundException.class);
    }

    @Test
    void updateAppuser_given_dto_then_update_username_and_password() {
        var updateAppuserDto = new UpdateAppuserDto("new username", "new password");
        var appuser = new Appuser("username", "password");
        var authenticatedUser = appuser.toSecurityUser();

        when(securityService.getUser()).thenReturn(authenticatedUser);
        when(appuserRepository.findById(authenticatedUser.id())).thenReturn(Optional.of(appuser));
        when(appuserRepository.existsByUsername(updateAppuserDto.username())).thenReturn(false);
        when(securityService.generateEncodedPassword(updateAppuserDto.password())).thenReturn("encoded-password");

        appuserService.updateAppuser(updateAppuserDto);

        verify(appuserRepository).save(appuserCaptor.capture());
        assertThat(appuserCaptor.getValue().getUsername()).isEqualTo("new username");
        assertThat(appuserCaptor.getValue().getPassword()).isEqualTo("encoded-password");
    }

    @Test
    void updateAppuser_given_dto_with_duplicate_username_then_throw_appuser_already_exists_exception() {
        var updateAppuserDto = new UpdateAppuserDto("duplicate username", "new password");
        var appuser = new Appuser("username", "password");
        var authenticatedUser = appuser.toSecurityUser();

        when(securityService.getUser()).thenReturn(authenticatedUser);
        when(appuserRepository.findById(authenticatedUser.id())).thenReturn(Optional.of(appuser));
        when(appuserRepository.existsByUsername(updateAppuserDto.username())).thenReturn(true);

        assertThatThrownBy(() -> appuserService.updateAppuser(updateAppuserDto)).isInstanceOf(AppuserAlreadyExistsException.class);
    }

    @Test
    void deleteAppuser_given_authenticated_user_then_delete_appuser() {
        var appuser = new Appuser("username", "password");
        var authenticatedUser = appuser.toSecurityUser();

        when(securityService.getUser()).thenReturn(authenticatedUser);
        when(appuserRepository.findById(authenticatedUser.id())).thenReturn(Optional.of(appuser));

        appuserService.deleteAppuser();

        verify(appuserRepository).deleteById(appuser.getId());
        verify(appuserPublisher).publish(new AppuserDeletedEvent(appuser.getId()));
    }

}