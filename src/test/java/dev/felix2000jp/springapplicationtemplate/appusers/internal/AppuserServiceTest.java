package dev.felix2000jp.springapplicationtemplate.appusers.internal;

import dev.felix2000jp.springapplicationtemplate.appusers.internal.dtos.AppuserDTO;
import dev.felix2000jp.springapplicationtemplate.appusers.internal.dtos.AuthenticatedAppuserDTO;
import dev.felix2000jp.springapplicationtemplate.appusers.internal.dtos.CreateAppuserDTO;
import dev.felix2000jp.springapplicationtemplate.appusers.internal.dtos.UpdateAppuserDTO;
import dev.felix2000jp.springapplicationtemplate.appusers.internal.exceptions.AppuserConflictException;
import dev.felix2000jp.springapplicationtemplate.appusers.internal.exceptions.AppuserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppuserServiceTest {

    @Spy
    private AppuserMapper appuserMapper = new AppuserMapperImpl();
    @Mock
    private AppuserRepository appuserRepository;
    @Mock
    private ApplicationEventPublisher events;
    @Spy
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Mock
    private JwtEncoder jwtEncoder;
    @Spy
    @InjectMocks
    private AppuserService appuserService;

    private Appuser appuser;
    private AppuserDTO appuserDTO;
    private AuthenticatedAppuserDTO authenticatedAppuserDTO;

    @BeforeEach
    void setUp() {
        appuser = new Appuser(UUID.randomUUID(), "Username", "Password");
        appuser.addApplicationAuthority();

        appuserDTO = new AppuserDTO(appuser.getId(), appuser.getUsername(), appuser.getAuthoritiesScopeValues());
        authenticatedAppuserDTO = new AuthenticatedAppuserDTO(appuserDTO.id(), appuserDTO.username(), appuserDTO.authorities());
    }

    @Test
    void find_should_return_appuser_when_appuser_is_found() {
        doReturn(authenticatedAppuserDTO).when(appuserService).getAuthenticatedAppuserDTO();
        when(appuserRepository.findById(appuser.getId())).thenReturn(Optional.of(appuser));

        var actual = appuserService.find();

        assertThat(actual).usingRecursiveComparison().isEqualTo(appuserDTO);
    }

    @Test
    void find_should_throw_not_found_when_appuser_is_not_found() {
        doReturn(authenticatedAppuserDTO).when(appuserService).getAuthenticatedAppuserDTO();
        when(appuserRepository.findById(appuser.getId())).thenReturn(Optional.empty());

        var actual = catchThrowable(() -> appuserService.find());

        assertThat(actual).isInstanceOf(AppuserNotFoundException.class);
    }

    @Test
    void create_should_return_appuser_when_appuser_is_created() {
        var createAppuserDTO = new CreateAppuserDTO(appuser.getUsername(), appuser.getPassword());

        when(appuserRepository.existsByUsername(createAppuserDTO.username())).thenReturn(false);
        when(appuserRepository.save(any(Appuser.class))).thenReturn(appuser);

        var actual = appuserService.create(createAppuserDTO);

        assertThat(actual).usingRecursiveComparison().isEqualTo(appuserDTO);
    }

    @Test
    void create_should_throw_conflict_when_username_already_exists() {
        var createAppuserDTO = new CreateAppuserDTO(appuser.getUsername(), appuser.getPassword());

        when(appuserRepository.existsByUsername(createAppuserDTO.username())).thenReturn(true);

        var actual = catchThrowable(() -> appuserService.create(createAppuserDTO));

        assertThat(actual).isInstanceOf(AppuserConflictException.class);
    }

    @Test
    void update_should_return_appuser_when_username_and_password_are_updated() {
        var updateAppuserDTO = new UpdateAppuserDTO("new username", "new password");

        doReturn(authenticatedAppuserDTO).when(appuserService).verifyAuthenticatedAppuserDTO();
        when(appuserRepository.findById(appuser.getId())).thenReturn(Optional.of(appuser));
        when(appuserRepository.existsByUsername(updateAppuserDTO.username())).thenReturn(false);
        when(appuserRepository.save(any(Appuser.class))).thenReturn(appuser);

        var actual = appuserService.update(updateAppuserDTO);

        var expected = new AppuserDTO(appuser.getId(), updateAppuserDTO.username(), Set.of("APPLICATION"));
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void update_should_return_appuser_when_only_password_is_updated() {
        var updateAppuserDTO = new UpdateAppuserDTO(appuser.getUsername(), "new password");

        doReturn(authenticatedAppuserDTO).when(appuserService).verifyAuthenticatedAppuserDTO();
        when(appuserRepository.findById(appuser.getId())).thenReturn(Optional.of(appuser));
        when(appuserRepository.existsByUsername(updateAppuserDTO.username())).thenReturn(true);
        when(appuserRepository.save(any(Appuser.class))).thenReturn(appuser);

        var actual = appuserService.update(updateAppuserDTO);

        var expected = new AppuserDTO(appuser.getId(), updateAppuserDTO.username(), Set.of("APPLICATION"));
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void update_should_throw_not_found_when_appuser_is_not_found() {
        var updateAppuserDTO = new UpdateAppuserDTO(appuser.getUsername(), "new password");

        doReturn(authenticatedAppuserDTO).when(appuserService).verifyAuthenticatedAppuserDTO();
        when(appuserRepository.findById(appuser.getId())).thenReturn(Optional.empty());

        var actual = catchThrowable(() -> appuserService.update(updateAppuserDTO));

        assertThat(actual).isInstanceOf(AppuserNotFoundException.class);
    }

    @Test
    void update_should_throw_conflict_when_new_username_already_exists() {
        var updateAppuserDTO = new UpdateAppuserDTO("new username", "new password");

        doReturn(authenticatedAppuserDTO).when(appuserService).verifyAuthenticatedAppuserDTO();
        when(appuserRepository.findById(appuser.getId())).thenReturn(Optional.of(appuser));
        when(appuserRepository.existsByUsername(updateAppuserDTO.username())).thenReturn(true);

        var actual = catchThrowable(() -> appuserService.update(updateAppuserDTO));

        assertThat(actual).isInstanceOf(AppuserConflictException.class);
    }

    @Test
    void delete_should_return_appuser_when_appuser_is_deleted() {
        doReturn(authenticatedAppuserDTO).when(appuserService).verifyAuthenticatedAppuserDTO();
        when(appuserRepository.findById(appuser.getId())).thenReturn(Optional.of(appuser));

        var actual = appuserService.delete();

        assertThat(actual).usingRecursiveComparison().isEqualTo(appuserDTO);
    }

    @Test
    void delete_should_throw_not_found_when_appuser_is_not_found() {
        doReturn(authenticatedAppuserDTO).when(appuserService).verifyAuthenticatedAppuserDTO();
        when(appuserRepository.findById(appuser.getId())).thenReturn(Optional.empty());

        var actual = catchThrowable(() -> appuserService.delete());

        assertThat(actual).isInstanceOf(AppuserNotFoundException.class);
    }

}
