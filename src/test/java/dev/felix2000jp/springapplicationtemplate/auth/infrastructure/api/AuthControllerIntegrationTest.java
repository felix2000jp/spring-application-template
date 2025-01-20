package dev.felix2000jp.springapplicationtemplate.auth.infrastructure.api;

import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.CreateAppuserDto;
import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.UpdatePasswordDto;
import dev.felix2000jp.springapplicationtemplate.auth.domain.Appuser;
import dev.felix2000jp.springapplicationtemplate.auth.domain.AppuserRepository;
import dev.felix2000jp.springapplicationtemplate.shared.SecurityService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@ApplicationModuleTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class AuthControllerIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres");

    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private SecurityService securityService;
    @Autowired
    private AppuserRepository appuserRepository;

    private Appuser appuser;

    @BeforeEach
    void setUp() {
        appuser = new Appuser("username", securityService.generateEncodedPassword("password"));
        appuser.addScopeAdmin();
        appuserRepository.save(appuser);
    }

    @AfterEach
    void tearDown() {
        appuserRepository.deleteById(appuser.getId());
    }

    @Test
    void login_given_user_then_return_login_token() {
        var loginTokenEntity = testRestTemplate.withBasicAuth("username", "password").exchange(
                "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(null),
                String.class
        );

        assertThat(loginTokenEntity.getStatusCode().value()).isEqualTo(200);
        assertThat(loginTokenEntity.getBody()).isNotBlank();
    }

    @Test
    void createAppuser_given_username_and_password_then_create_appuser() {
        var createAppuserEntity = testRestTemplate.exchange(
                "/auth/register",
                HttpMethod.POST,
                new HttpEntity<>(new CreateAppuserDto("new username", "password"), null),
                Void.class
        );

        assertThat(createAppuserEntity.getStatusCode().value()).isEqualTo(201);

        var createdAppuser = appuserRepository.findByUsername("new username");
        assertThat(createdAppuser).isPresent();
    }

    @Test
    void updatePassword_given_user_then_update_password() {
        var updatePasswordEntity = testRestTemplate.withBasicAuth("username", "password").exchange(
                "/auth/password",
                HttpMethod.PUT,
                new HttpEntity<>(new UpdatePasswordDto("new password"), null),
                Void.class
        );

        assertThat(updatePasswordEntity.getStatusCode().value()).isEqualTo(204);

        var updatedAppuser = appuserRepository.findById(appuser.getId());
        assertThat(updatedAppuser).isPresent();
        assertThat(updatedAppuser.get().getPassword()).isNotEqualTo(appuser.getPassword());
    }

}
