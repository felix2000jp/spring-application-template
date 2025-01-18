package dev.felix2000jp.springapplicationtemplate.auth.api;

import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.CreateAppuserDto;
import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.UpdatePasswordDto;
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
import org.springframework.security.web.server.csrf.DefaultCsrfToken;
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

    private HttpHeaders headers;

    @BeforeEach
    void setUp() {
        var csrfTokenEntity = testRestTemplate.exchange(
                "/auth/csrf",
                HttpMethod.GET,
                new HttpEntity<>(null),
                DefaultCsrfToken.class
        );

        assertThat(csrfTokenEntity.getStatusCode().value()).isEqualTo(200);
        assertThat(csrfTokenEntity.getBody()).isNotNull();

        headers = new HttpHeaders();
        headers.add("X-Csrf-Token", csrfTokenEntity.getBody().getToken());
        headers.addAll("Cookie", csrfTokenEntity.getHeaders().getOrEmpty("SET-COOKIE"));
    }

    @Test
    void login_given_user_then_return_login_token() {
        var createUserEntity = testRestTemplate.exchange(
                "/auth/register",
                HttpMethod.POST,
                new HttpEntity<>(new CreateAppuserDto("username", "password"), headers),
                Void.class
        );

        assertThat(createUserEntity.getStatusCode().value()).isEqualTo(201);

        var loginTokenEntity = testRestTemplate.withBasicAuth("username", "password").exchange(
                "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(headers),
                String.class
        );

        assertThat(loginTokenEntity.getStatusCode().value()).isEqualTo(200);
        assertThat(loginTokenEntity.getBody()).isNotBlank();
    }

    @Test
    void updatePassword_given_user_then_update_password() {
        var createUserEntity = testRestTemplate.exchange(
                "/auth/register",
                HttpMethod.POST,
                new HttpEntity<>(new CreateAppuserDto("username", "password"), headers),
                Void.class
        );

        assertThat(createUserEntity.getStatusCode().value()).isEqualTo(201);

        var updatePasswordEntity = testRestTemplate.withBasicAuth("username", "password").exchange(
                "/auth/password",
                HttpMethod.PUT,
                new HttpEntity<>(new UpdatePasswordDto("new password"), headers),
                Void.class
        );

        assertThat(updatePasswordEntity.getStatusCode().value()).isEqualTo(204);

        var loginTokenEntity = testRestTemplate.withBasicAuth("username", "password").exchange(
                "/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(headers),
                String.class
        );

        assertThat(loginTokenEntity.getStatusCode().value()).isEqualTo(401);
    }

}
