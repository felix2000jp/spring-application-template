package dev.felix2000jp.springapplicationtemplate.auth.application;

import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.UpdateAppuserDto;
import dev.felix2000jp.springapplicationtemplate.auth.domain.Appuser;
import dev.felix2000jp.springapplicationtemplate.auth.domain.AppuserRepository;
import dev.felix2000jp.springapplicationtemplate.auth.domain.exceptions.AppuserAlreadyExistsException;
import dev.felix2000jp.springapplicationtemplate.auth.domain.exceptions.AppuserNotFoundException;
import dev.felix2000jp.springapplicationtemplate.shared.SecurityService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppuserServiceImplTest {

    @Mock
    private AppuserRepository appuserRepository;
    @Spy
    private AppuserMapper appuserMapper;
    @Mock
    private SecurityService securityService;
    @Mock
    private ApplicationEventPublisher events;
    @InjectMocks
    private AppuserServiceImpl appuserService;

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
        var authenticatedUser = new SecurityService.User(
                appuser.getId(),
                appuser.getUsername(),
                appuser.getAuthoritiesScopes()
        );

        when(securityService.getUser()).thenReturn(authenticatedUser);
        when(appuserRepository.findById(authenticatedUser.id())).thenReturn(Optional.of(appuser));

        var actual = appuserService.getAppuserForCurrentUser();

        assertThat(actual.id()).isEqualTo(appuser.getId());
        assertThat(actual.username()).isEqualTo(appuser.getUsername());
        assertThat(actual.scopes()).isEqualTo(appuser.getAuthoritiesScopes());
    }

    @Test
    void getAppuserForCurrentUser_given_not_found_authenticated_user_then_throw_appuser_not_found_exception() {
        var authenticatedUser = new SecurityService.User(UUID.randomUUID(), "username", Set.of());

        when(securityService.getUser()).thenReturn(authenticatedUser);
        when(appuserRepository.findById(authenticatedUser.id())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appuserService.getAppuserForCurrentUser()).isInstanceOf(AppuserNotFoundException.class);
    }

    @Test
    void updateAppuserForCurrentUser_given_dto_with_new_username_then_update_appuser() {
        var appuser = new Appuser("username", "password");
        var updateAppuserDto = new UpdateAppuserDto("new username");
        var authenticatedUser = new SecurityService.User(
                appuser.getId(),
                appuser.getUsername(),
                appuser.getAuthoritiesScopes()
        );

        when(securityService.getUser()).thenReturn(authenticatedUser);
        when(appuserRepository.findById(authenticatedUser.id())).thenReturn(Optional.of(appuser));
        when(appuserRepository.existsByUsername(updateAppuserDto.username())).thenReturn(false);

        var actual = appuserService.updateAppuserForCurrentUser(updateAppuserDto);

        assertThat(actual.username()).isEqualTo(updateAppuserDto.username());
    }

    @Test
    void updateAppuserForCurrentUser_given_dto_with_old_username_then_update_appuser() {
        var appuser = new Appuser("username", "password");
        var updateAppuserDto = new UpdateAppuserDto("username");
        var authenticatedUser = new SecurityService.User(
                appuser.getId(),
                appuser.getUsername(),
                appuser.getAuthoritiesScopes()
        );

        when(securityService.getUser()).thenReturn(authenticatedUser);
        when(appuserRepository.findById(authenticatedUser.id())).thenReturn(Optional.of(appuser));
        when(appuserRepository.existsByUsername(updateAppuserDto.username())).thenReturn(true);

        var actual = appuserService.updateAppuserForCurrentUser(updateAppuserDto);

        assertThat(actual.username()).isEqualTo(updateAppuserDto.username());
    }

    @Test
    void updateAppuserForCurrentUser_given_not_found_authenticated_user_then_throw_appuser_not_found_exception() {
        var updateAppuserDto = new UpdateAppuserDto("username");
        var authenticatedUser = new SecurityService.User(UUID.randomUUID(), "username", Set.of());

        when(securityService.getUser()).thenReturn(authenticatedUser);
        when(appuserRepository.findById(authenticatedUser.id())).thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> appuserService.updateAppuserForCurrentUser(updateAppuserDto)
        ).isInstanceOf(AppuserNotFoundException.class);
    }

    @Test
    void updateAppuserForCurrentUser_given_dto_with_duplicate_username_then_throw_appuser_already_exists_exception() {
        var appuser = new Appuser("username", "password");
        var updateAppuserDto = new UpdateAppuserDto("new username");
        var authenticatedUser = new SecurityService.User(
                appuser.getId(),
                appuser.getUsername(),
                appuser.getAuthoritiesScopes()
        );

        when(securityService.getUser()).thenReturn(authenticatedUser);
        when(appuserRepository.findById(authenticatedUser.id())).thenReturn(Optional.of(appuser));
        when(appuserRepository.existsByUsername(updateAppuserDto.username())).thenReturn(true);

        assertThatThrownBy(
                () -> appuserService.updateAppuserForCurrentUser(updateAppuserDto)
        ).isInstanceOf(AppuserAlreadyExistsException.class);
    }

    @Test
    void deleteAppuserForCurrentUser_given_authenticated_user_then_delete_appuser() {
        var appuser = new Appuser("username", "password");
        var authenticatedUser = new SecurityService.User(
                appuser.getId(),
                appuser.getUsername(),
                appuser.getAuthoritiesScopes()
        );

        when(securityService.getUser()).thenReturn(authenticatedUser);
        when(appuserRepository.findById(authenticatedUser.id())).thenReturn(Optional.of(appuser));

        var actual = appuserService.deleteAppuserForCurrentUser();

        assertThat(actual.id()).isEqualTo(appuser.getId());
        assertThat(actual.username()).isEqualTo(appuser.getUsername());
        assertThat(actual.scopes()).isEqualTo(appuser.getAuthoritiesScopes());
    }

    @Test
    void deleteAppuserForCurrentUser_given_not_found_authenticated_user_then_throw_appuser_not_found_exception() {
        var appuser = new Appuser("username", "password");
        var authenticatedUser = new SecurityService.User(
                appuser.getId(),
                appuser.getUsername(),
                appuser.getAuthoritiesScopes()
        );

        when(securityService.getUser()).thenReturn(authenticatedUser);
        when(appuserRepository.findById(authenticatedUser.id())).thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> appuserService.deleteAppuserForCurrentUser()
        ).isInstanceOf(AppuserNotFoundException.class);
    }

}