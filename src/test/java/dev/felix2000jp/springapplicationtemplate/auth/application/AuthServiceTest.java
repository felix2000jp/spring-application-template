package dev.felix2000jp.springapplicationtemplate.auth.application;

import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.CreateAppuserDto;
import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.UpdateAppuserDto;
import dev.felix2000jp.springapplicationtemplate.auth.application.events.AppuserDeletedEvent;
import dev.felix2000jp.springapplicationtemplate.auth.domain.Appuser;
import dev.felix2000jp.springapplicationtemplate.auth.domain.AppuserRepository;
import dev.felix2000jp.springapplicationtemplate.auth.domain.exceptions.AppuserAlreadyExistsException;
import dev.felix2000jp.springapplicationtemplate.shared.security.SecurityService;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AppuserRepository appuserRepository;
    @Mock
    private SecurityService securityService;
    @InjectMocks
    private AuthService authService;

    @Captor
    private ArgumentCaptor<Appuser> appuserCaptor;

    @Test
    void register_given_dto_then_create_user() {
        var createAppuserDto = new CreateAppuserDto("username", "password");

        when(appuserRepository.existsByUsername(createAppuserDto.username())).thenReturn(false);
        when(securityService.generateEncodedPassword(createAppuserDto.password())).thenReturn("encoded-password");

        authService.register(createAppuserDto);

        verify(appuserRepository).save(appuserCaptor.capture());

        assertThat(appuserCaptor.getValue().getUsername()).isEqualTo(createAppuserDto.username());
        assertThat(appuserCaptor.getValue().getPassword()).isEqualTo("encoded-password");
    }

    @Test
    void register_already_exists_exception() {
        var createAppuserDto = new CreateAppuserDto("username", "password");

        when(appuserRepository.existsByUsername(createAppuserDto.username())).thenReturn(true);

        assertThatThrownBy(() -> authService.register(createAppuserDto)).isInstanceOf(AppuserAlreadyExistsException.class);
    }

}
