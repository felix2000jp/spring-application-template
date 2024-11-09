package dev.felix2000jp.springapplicationtemplate.appusers.internal;

import dev.felix2000jp.springapplicationtemplate.TestcontainersConfiguration;
import dev.felix2000jp.springapplicationtemplate.appusers.internal.dtos.AppuserDTO;
import dev.felix2000jp.springapplicationtemplate.appusers.internal.dtos.CreateAppuserDTO;
import dev.felix2000jp.springapplicationtemplate.appusers.internal.dtos.UpdateAppuserDTO;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.security.web.csrf.DefaultCsrfToken;

import static org.assertj.core.api.Assertions.assertThat;

@Import({TestcontainersConfiguration.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ApplicationModuleTest(extraIncludes = {"shared"}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AppuserControllerIntegrationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    private static final HttpHeaders authenticatedHeaders = new HttpHeaders();

    @Order(1)
    @Test
    void should_get_csrf_token() {
        var csrfEntity = testRestTemplate.exchange(
                "/api/appusers/csrf",
                HttpMethod.GET,
                null,
                DefaultCsrfToken.class
        );
        var csrfEntityStatusCode = csrfEntity.getStatusCode().value();
        var csrfEntityBody = csrfEntity.getBody();
        var csrfEntityHeaders = csrfEntity.getHeaders();

        assertThat(csrfEntityStatusCode).isEqualTo(200);
        assertThat(csrfEntityBody).isNotNull();
        assertThat(csrfEntityHeaders).isNotNull();

        authenticatedHeaders.addAll("Cookie", csrfEntityHeaders.getOrEmpty("SET-COOKIE"));
        authenticatedHeaders.add("X-Csrf-Token", csrfEntityBody.getToken());
    }

    @Order(2)
    @Test
    void should_create_appuser_when_csrf_token_is_present() {
        var createEntity = testRestTemplate.exchange(
                "/api/appusers",
                HttpMethod.POST,
                new HttpEntity<>(new CreateAppuserDTO("username", "password"), authenticatedHeaders),
                AppuserDTO.class
        );
        var createEntityStatusCode = createEntity.getStatusCode().value();
        var createEntityBody = createEntity.getBody();

        assertThat(createEntityStatusCode).isEqualTo(201);
        assertThat(createEntityBody).isNotNull();
    }

    @Order(3)
    @Test
    void should_generate_bearer_token_when_csrf_token_is_present_and_credentials_are_correct() {
        var tokenEntity = testRestTemplate
                .withBasicAuth("username", "password")
                .exchange("/api/appusers/token", HttpMethod.POST, new HttpEntity<>(authenticatedHeaders), String.class);
        var tokenEntityStatusCode = tokenEntity.getStatusCode().value();
        var tokenEntityBody = tokenEntity.getBody();

        assertThat(tokenEntityStatusCode).isEqualTo(200);
        assertThat(tokenEntityBody).isNotNull();

        authenticatedHeaders.add("Authorization", "Bearer " + tokenEntityBody);
    }

    @Order(4)
    @Test
    void should_find_appuser_when_appuser_exists_and_request_is_authenticated() {
        var findEntity = testRestTemplate.exchange(
                "/api/appusers",
                HttpMethod.GET,
                new HttpEntity<>(authenticatedHeaders),
                AppuserDTO.class
        );
        var findEntityStatusCode = findEntity.getStatusCode().value();
        var findEntityBody = findEntity.getBody();

        assertThat(findEntityStatusCode).isEqualTo(200);
        assertThat(findEntityBody).isNotNull();
    }

    @Order(5)
    @Test
    void should_not_find_appuser_when_request_is_not_authenticated() {
        var findEntity = testRestTemplate.exchange("/api/appusers", HttpMethod.GET, null, AppuserDTO.class);
        var findEntityStatusCode = findEntity.getStatusCode().value();
        var findEntityBody = findEntity.getBody();

        assertThat(findEntityStatusCode).isEqualTo(401);
        assertThat(findEntityBody).isNull();
    }

    @Order(6)
    @Test
    void should_update_appuser_when_appuser_exists_and_request_is_authenticated() {
        var updateEntity = testRestTemplate.exchange(
                "/api/appusers",
                HttpMethod.PUT,
                new HttpEntity<>(new UpdateAppuserDTO("username", "new password"), authenticatedHeaders),
                Void.class
        );
        var updateEntityStatusCode = updateEntity.getStatusCode().value();

        assertThat(updateEntityStatusCode).isEqualTo(204);
    }

    @Order(7)
    @Test
    void should_not_update_appuser_when_request_is_not_authenticated() {
        var updateEntity = testRestTemplate.exchange(
                "/api/appusers",
                HttpMethod.PUT,
                new HttpEntity<>(new UpdateAppuserDTO("new username", "new password")),
                Void.class
        );
        var updateEntityStatusCode = updateEntity.getStatusCode().value();
        var updateEntityBody = updateEntity.getBody();

        assertThat(updateEntityStatusCode).isEqualTo(401);
        assertThat(updateEntityBody).isNull();
    }

    @Order(8)
    @Test
    void should_delete_appuser_when_appuser_exists_and_request_is_authenticated() {
        var deleteEntity = testRestTemplate.exchange(
                "/api/appusers",
                HttpMethod.DELETE,
                new HttpEntity<>(authenticatedHeaders),
                Void.class
        );
        var deleteEntityStatusCode = deleteEntity.getStatusCode().value();

        assertThat(deleteEntityStatusCode).isEqualTo(204);
    }

    @Order(8)
    @Test
    void should_not_delete_appuser_request_is_not_authenticated() {
        var deleteEntity = testRestTemplate.exchange(
                "/api/appusers",
                HttpMethod.DELETE,
                null,
                Void.class
        );
        var deleteEntityStatusCode = deleteEntity.getStatusCode().value();
        var deleteEntityBody = deleteEntity.getBody();

        assertThat(deleteEntityStatusCode).isEqualTo(401);
        assertThat(deleteEntityBody).isNull();
    }

}
