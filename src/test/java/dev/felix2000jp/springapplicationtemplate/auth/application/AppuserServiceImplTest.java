package dev.felix2000jp.springapplicationtemplate.auth.application;

import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.UpdateAppuserDTO;
import dev.felix2000jp.springapplicationtemplate.auth.domain.Appuser;
import dev.felix2000jp.springapplicationtemplate.auth.domain.AppuserRepository;
import dev.felix2000jp.springapplicationtemplate.auth.domain.exceptions.AppuserAlreadyExistsException;
import dev.felix2000jp.springapplicationtemplate.auth.domain.exceptions.AppuserNotFoundException;
import dev.felix2000jp.springapplicationtemplate.core.SecurityClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppuserServiceImplTest {

    @Mock
    private AppuserRepository appuserRepository;
    @Spy
    private AppuserMapper appuserMapper;
    @Mock
    private SecurityClient securityClient;
    @InjectMocks
    private AppuserServiceImpl appuserService;

    @Test
    void should_get_appusers_when_page_is_not_empty() {
        var appuser = new Appuser("username", "password");

        when(appuserRepository.getAll(0)).thenReturn(List.of(appuser));

        var actual = appuserService.getAll(0);
        var actualAppuser = actual.appusers().getFirst();

        assertEquals(1, actual.appusers().size());
        assertEquals(appuser.getId(), actualAppuser.id());
        assertEquals(appuser.getUsername(), actualAppuser.username());
        assertEquals(appuser.getAuthoritiesScopeValues(), actualAppuser.authorities());
    }

    @Test
    void should_not_get_appusers_when_page_is_empty() {
        when(appuserRepository.getAll(0)).thenReturn(List.of());

        var actual = appuserService.getAll(0);

        assertEquals(0, actual.appusers().size());
    }

    @Test
    void should_get_authenticated_appuser_when_appuser_exists() {
        var appuser = new Appuser("username", "password");
        var authenticatedUser = new SecurityClient.User(
                appuser.getId(),
                appuser.getUsername(),
                appuser.getAuthoritiesScopeValues()
        );

        when(securityClient.getUser()).thenReturn(authenticatedUser);
        when(appuserRepository.getById(authenticatedUser.id())).thenReturn(appuser);

        var actual = appuserService.getAuthenticated();

        assertEquals(appuser.getId(), actual.id());
        assertEquals(appuser.getUsername(), actual.username());
        assertEquals(appuser.getAuthoritiesScopeValues(), actual.authorities());
    }

    @Test
    void should_not_get_authenticated_appuser_when_appuser_does_not_exist() {
        var authenticatedUser = new SecurityClient.User(UUID.randomUUID(), "username", Set.of());

        when(securityClient.getUser()).thenReturn(authenticatedUser);
        when(appuserRepository.getById(authenticatedUser.id())).thenReturn(null);

        assertThrows(AppuserNotFoundException.class, () -> appuserService.getAuthenticated());
    }

    @Test
    void should_update_authenticated_appuser_when_username_is_unique() {
        var appuser = new Appuser("username", "password");
        var updateAppuserDTO = new UpdateAppuserDTO("new username");
        var authenticatedUser = new SecurityClient.User(
                appuser.getId(),
                appuser.getUsername(),
                appuser.getAuthoritiesScopeValues()
        );

        when(securityClient.getUser()).thenReturn(authenticatedUser);
        when(appuserRepository.getById(authenticatedUser.id())).thenReturn(appuser);
        when(appuserRepository.existsByUsername(updateAppuserDTO.username())).thenReturn(false);

        var actual = appuserService.updateAuthenticated(updateAppuserDTO);

        assertEquals(appuser.getId(), actual.id());
        assertEquals(updateAppuserDTO.username(), actual.username());
        assertEquals(appuser.getAuthoritiesScopeValues(), actual.authorities());
    }

    @Test
    void should_update_authenticated_appuser_when_username_is_not_changed() {
        var appuser = new Appuser("username", "password");
        var updateAppuserDTO = new UpdateAppuserDTO("username");
        var authenticatedUser = new SecurityClient.User(
                appuser.getId(),
                appuser.getUsername(),
                appuser.getAuthoritiesScopeValues()
        );

        when(securityClient.getUser()).thenReturn(authenticatedUser);
        when(appuserRepository.getById(authenticatedUser.id())).thenReturn(appuser);
        when(appuserRepository.existsByUsername(updateAppuserDTO.username())).thenReturn(true);

        var actual = appuserService.updateAuthenticated(updateAppuserDTO);

        assertEquals(appuser.getId(), actual.id());
        assertEquals(updateAppuserDTO.username(), actual.username());
        assertEquals(appuser.getAuthoritiesScopeValues(), actual.authorities());
    }

    @Test
    void should_fail_to_update_authenticated_appuser_when_appuser_does_not_exists() {
        var updateAppuserDTO = new UpdateAppuserDTO("username");
        var authenticatedUser = new SecurityClient.User(UUID.randomUUID(), "username", Set.of());

        when(securityClient.getUser()).thenReturn(authenticatedUser);
        when(appuserRepository.getById(authenticatedUser.id())).thenReturn(null);

        assertThrows(AppuserNotFoundException.class, () -> appuserService.updateAuthenticated(updateAppuserDTO));
    }

    @Test
    void should_fail_to_update_authenticated_appuser_when_username_already_exists() {
        var appuser = new Appuser("username", "password");
        var updateAppuserDTO = new UpdateAppuserDTO("new username");
        var authenticatedUser = new SecurityClient.User(
                appuser.getId(),
                appuser.getUsername(),
                appuser.getAuthoritiesScopeValues()
        );

        when(securityClient.getUser()).thenReturn(authenticatedUser);
        when(appuserRepository.getById(authenticatedUser.id())).thenReturn(appuser);
        when(appuserRepository.existsByUsername(updateAppuserDTO.username())).thenReturn(true);

        assertThrows(AppuserAlreadyExistsException.class, () -> appuserService.updateAuthenticated(updateAppuserDTO));
    }

    @Test
    void should_delete_authenticated_appuser_when_appuser_exists() {
        var appuser = new Appuser("username", "password");
        var authenticatedUser = new SecurityClient.User(
                appuser.getId(),
                appuser.getUsername(),
                appuser.getAuthoritiesScopeValues()
        );

        when(securityClient.getUser()).thenReturn(authenticatedUser);
        when(appuserRepository.getById(authenticatedUser.id())).thenReturn(appuser);

        var actual = appuserService.deleteAuthenticated();

        assertEquals(appuser.getId(), actual.id());
        assertEquals(appuser.getUsername(), actual.username());
        assertEquals(appuser.getAuthoritiesScopeValues(), actual.authorities());
    }

    @Test
    void should_fail_to_delete_authenticated_appuser_when_appuser_does_not_exist() {
        var appuser = new Appuser("username", "password");
        var authenticatedUser = new SecurityClient.User(
                appuser.getId(),
                appuser.getUsername(),
                appuser.getAuthoritiesScopeValues()
        );

        when(securityClient.getUser()).thenReturn(authenticatedUser);
        when(appuserRepository.getById(authenticatedUser.id())).thenReturn(null);

        assertThrows(AppuserNotFoundException.class, () -> appuserService.deleteAuthenticated());
    }

}