package dev.felix2000jp.springapplicationtemplate.appusers.internal;

import dev.felix2000jp.springapplicationtemplate.appusers.internal.dtos.AppuserDto;
import dev.felix2000jp.springapplicationtemplate.appusers.internal.dtos.CreateAppuserDto;
import dev.felix2000jp.springapplicationtemplate.appusers.internal.dtos.UpdateAppuserDto;
import dev.felix2000jp.springapplicationtemplate.appusers.internal.exceptions.AppuserConflictException;
import dev.felix2000jp.springapplicationtemplate.appusers.internal.exceptions.AppuserNotFoundException;
import dev.felix2000jp.springapplicationtemplate.shared.AuthorityValue;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppuserServiceTest {

    @Mock
    private JwtEncoder jwtEncoder;
    @Spy
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Spy
    private AppuserMapper appuserMapper = new AppuserMapperImpl();
    @Mock
    private AppuserRepository appuserRepository;
    @Mock
    private ApplicationEventPublisher events;
    @InjectMocks
    private AppuserService appuserService;

    private Appuser appuser;
    private AppuserDto appuserDto;

    @BeforeEach
    void setUp() {
        appuser = new Appuser(UUID.randomUUID(), "Username", "Password", AuthorityValue.APPLICATION);
        appuserDto = new AppuserDto(appuser.getId(), appuser.getUsername(), appuser.getAuthorityValues());
    }

    @Test
    void find_should_return_appuser_when_appuser_is_found() {
        when(appuserRepository.findById(appuser.getId())).thenReturn(Optional.of(appuser));

        var actual = appuserService.find(appuser);

        assertThat(actual).usingRecursiveComparison().isEqualTo(appuserDto);
    }

    @Test
    void find_should_throw_not_found_when_appuser_is_not_found() {
        when(appuserRepository.findById(appuser.getId())).thenReturn(Optional.empty());

        var actual = catchThrowable(() -> appuserService.find(appuser));

        assertThat(actual).isInstanceOf(AppuserNotFoundException.class);
    }

    @Test
    void create_should_return_appuser_when_appuser_is_created() {
        var createAppuserDto = new CreateAppuserDto(appuser.getUsername(), appuser.getPassword());

        when(appuserRepository.existsByUsername(createAppuserDto.username())).thenReturn(false);
        when(appuserRepository.save(any(Appuser.class))).thenReturn(appuser);

        var actual = appuserService.create(createAppuserDto);

        assertThat(actual).usingRecursiveComparison().isEqualTo(appuserDto);
    }

    @Test
    void create_should_throw_conflict_when_username_already_exists() {
        var createAppuserDto = new CreateAppuserDto(appuser.getUsername(), appuser.getPassword());

        when(appuserRepository.existsByUsername(createAppuserDto.username())).thenReturn(true);

        var actual = catchThrowable(() -> appuserService.create(createAppuserDto));

        assertThat(actual).isInstanceOf(AppuserConflictException.class);
    }

    @Test
    void update_should_return_appuser_when_username_and_password_are_updated() {
        var updateAppuserDto = new UpdateAppuserDto("new username", "new password");

        when(appuserRepository.findById(appuser.getId())).thenReturn(Optional.of(appuser));
        when(appuserRepository.existsByUsername(updateAppuserDto.username())).thenReturn(false);
        when(appuserRepository.save(any(Appuser.class))).thenReturn(appuser);

        var actual = appuserService.update(appuser, updateAppuserDto);

        var expected = new AppuserDto(appuser.getId(), updateAppuserDto.username(), List.of(AuthorityValue.APPLICATION));
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void update_should_return_appuser_when_only_password_is_updated() {
        var updateAppuserDto = new UpdateAppuserDto(appuser.getUsername(), "new password");

        when(appuserRepository.findById(appuser.getId())).thenReturn(Optional.of(appuser));
        when(appuserRepository.existsByUsername(updateAppuserDto.username())).thenReturn(true);
        when(appuserRepository.save(any(Appuser.class))).thenReturn(appuser);

        var actual = appuserService.update(appuser, updateAppuserDto);

        var expected = new AppuserDto(appuser.getId(), updateAppuserDto.username(), List.of(AuthorityValue.APPLICATION));
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void update_should_throw_not_found_when_appuser_is_not_found() {
        var updateAppuserDto = new UpdateAppuserDto(appuser.getUsername(), "new password");

        when(appuserRepository.findById(appuser.getId())).thenReturn(Optional.empty());

        var actual = catchThrowable(() -> appuserService.update(appuser, updateAppuserDto));

        assertThat(actual).isInstanceOf(AppuserNotFoundException.class);
    }

    @Test
    void update_should_throw_conflict_when_new_username_already_exists() {
        var updateAppuserDto = new UpdateAppuserDto("new username", "new password");

        when(appuserRepository.findById(appuser.getId())).thenReturn(Optional.of(appuser));
        when(appuserRepository.existsByUsername(updateAppuserDto.username())).thenReturn(true);

        var actual = catchThrowable(() -> appuserService.update(appuser, updateAppuserDto));

        assertThat(actual).isInstanceOf(AppuserConflictException.class);
    }

    @Test
    void delete_should_return_appuser_when_appuser_is_deleted() {
        when(appuserRepository.findById(appuser.getId())).thenReturn(Optional.of(appuser));

        var actual = appuserService.delete(appuser);

        assertThat(actual).usingRecursiveComparison().isEqualTo(appuserDto);
    }

    @Test
    void delete_should_throw_not_found_when_appuser_is_not_found() {
        when(appuserRepository.findById(appuser.getId())).thenReturn(Optional.empty());

        var actual = catchThrowable(() -> appuserService.delete(appuser));

        assertThat(actual).isInstanceOf(AppuserNotFoundException.class);
    }

    @Test
    void verifyAppuserExistsById_should_return_when_appuser_exists() {
        when(appuserRepository.existsById(appuser.getId())).thenReturn(true);

        appuserService.verifyExistsById(appuser.getId());
    }

    @Test
    void verifyAppuserExistsById_should_throw_not_found_when_appuser_does_not_exist() {
        when(appuserRepository.existsById(appuser.getId())).thenReturn(false);

        var actual = catchThrowable(() -> appuserService.verifyExistsById(appuser.getId()));

        assertThat(actual).isInstanceOf(AppuserNotFoundException.class);
    }

}
