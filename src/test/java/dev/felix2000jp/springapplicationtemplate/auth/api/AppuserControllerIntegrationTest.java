package dev.felix2000jp.springapplicationtemplate.auth.api;

import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.AppuserDto;
import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.AppuserListDto;
import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.UpdateAppuserDto;
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

        var loginTokenEntity = testRestTemplate.withBasicAuth("username", "password").exchange(
                "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(null),
                String.class
        );

        assertThat(loginTokenEntity.getStatusCode().value()).isEqualTo(200);
        assertThat(loginTokenEntity.getBody()).isNotBlank();

        headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + loginTokenEntity.getBody());
    }

    @AfterEach
    void tearDown() {
        appuserRepository.deleteById(appuser.getId());
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
        assertThat(getAppuserEntity.getBody().appusers()).hasSize(1);
    }

    @Test
    void getAppuserForCurrentUser_given_user_then_return_respective_appuser() {
        var getAppuserEntity = testRestTemplate.exchange(
                "/api/appusers/me",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                AppuserDto.class
        );

        assertThat(getAppuserEntity.getStatusCode().value()).isEqualTo(200);
        assertThat(getAppuserEntity.getBody()).isNotNull();
    }

    @Test
    void updateAppuserForCurrentUser_given_user_then_update_appuser() {
        var updateAppuserEntity = testRestTemplate.exchange(
                "/api/appusers/me",
                HttpMethod.PUT,
                new HttpEntity<>(new UpdateAppuserDto("new username"), headers),
                Void.class
        );

        assertThat(updateAppuserEntity.getStatusCode().value()).isEqualTo(204);

        var updatedAppuser = appuserRepository.findById(appuser.getId());
        assertThat(updatedAppuser).isPresent();
        assertThat(updatedAppuser.get().getUsername()).isEqualTo("new username");
    }

    @Test
    void deleteAppuserForCurrentUser_given_user_then_delete_appuser() {
        var deleteAppuserEntity = testRestTemplate.exchange(
                "/api/appusers/me",
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                Void.class
        );

        assertThat(deleteAppuserEntity.getStatusCode().value()).isEqualTo(204);

        var deletedAppuser = appuserRepository.findById(appuser.getId());
        assertThat(deletedAppuser).isNotPresent();
    }

}
