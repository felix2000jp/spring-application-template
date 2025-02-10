package dev.felix2000jp.springapplicationtemplate.auth.infrastructure.api;

import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.AppuserDto;
import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.AppuserListDto;
import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.CreateAppuserDto;
import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.UpdateAppuserDto;
import dev.felix2000jp.springapplicationtemplate.auth.application.SecurityService;
import dev.felix2000jp.springapplicationtemplate.auth.domain.Appuser;
import dev.felix2000jp.springapplicationtemplate.auth.domain.AppuserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@ApplicationModuleTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class AppuserControllerIntegrationTest {

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
    private HttpHeaders headers;

    @BeforeEach
    void setUp() {
        appuser = new Appuser("username", securityService.generateEncodedPassword("password"));
        appuser.addScopeAdmin();
        appuserRepository.save(appuser);

        var token = securityService.generateToken(
                appuser.getUsername(),
                appuser.getId().toString(),
                String.join(" ", appuser.getAuthoritiesScopes())
        );
        headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
    }

    @AfterEach
    void tearDown() {
        appuserRepository.deleteById(appuser.getId());
    }

    @Test
    void login_given_user_then_return_login_token() {
        var loginTokenEntity = testRestTemplate.withBasicAuth("username", "password").exchange(
                "/api/appusers/login",
                HttpMethod.POST,
                new HttpEntity<>(null),
                String.class
        );

        assertThat(loginTokenEntity.getStatusCode().value()).isEqualTo(200);
        assertThat(loginTokenEntity.getBody()).isNotBlank();
    }

    @Test
    void register_given_username_and_password_then_create_new_user() {
        var createAppuserEntity = testRestTemplate.exchange(
                "/api/appusers/register",
                HttpMethod.POST,
                new HttpEntity<>(new CreateAppuserDto("new username", "password"), null),
                Void.class
        );

        assertThat(createAppuserEntity.getStatusCode().value()).isEqualTo(201);

        var createdAppuser = appuserRepository.findByUsername("new username");
        assertThat(createdAppuser).isPresent();
    }

    @Test
    void getAppusers_given_user_with_admin_authority_then_return_all_appusers() {
        var getAppuserEntity = testRestTemplate.exchange(
                "/api/appusers/admin",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                AppuserListDto.class
        );

        assertThat(getAppuserEntity.getStatusCode().value()).isEqualTo(200);
        assertThat(getAppuserEntity.getBody()).isNotNull();
    }

    @Test
    void getAppuserForCurrentUser_given_user_then_return_respective_appuser() {
        var getAppuserEntity = testRestTemplate.exchange(
                "/api/appusers",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                AppuserDto.class
        );

        assertThat(getAppuserEntity.getStatusCode().value()).isEqualTo(200);
        assertThat(getAppuserEntity.getBody()).isNotNull();
    }

    @Test
    void updateAppuser_given_user_then_update_username_and_password() {
        var updatePasswordEntity = testRestTemplate.exchange(
                "/api/appusers",
                HttpMethod.PUT,
                new HttpEntity<>(new UpdateAppuserDto("updated username", "updated password"), headers),
                Void.class
        );

        assertThat(updatePasswordEntity.getStatusCode().value()).isEqualTo(204);

        var updatedAppuser = appuserRepository.findById(appuser.getId());
        assertThat(updatedAppuser).isPresent();
        assertThat(updatedAppuser.get().getUsername()).isEqualTo("updated username");
        assertThat(updatedAppuser.get().getPassword()).isNotEqualTo(appuser.getPassword());
    }

    @Test
    void deleteAppuser_given_user_then_delete_appuser() {
        var deleteAppuserEntity = testRestTemplate.exchange(
                "/api/appusers",
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                Void.class
        );

        assertThat(deleteAppuserEntity.getStatusCode().value()).isEqualTo(204);

        var deletedAppuser = appuserRepository.findById(appuser.getId());
        assertThat(deletedAppuser).isNotPresent();
    }
}
