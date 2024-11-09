package dev.felix2000jp.springapplicationtemplate.appusers.internal;

import dev.felix2000jp.springapplicationtemplate.TestcontainersConfiguration;
import dev.felix2000jp.springapplicationtemplate.appusers.internal.dtos.AppuserDto;
import dev.felix2000jp.springapplicationtemplate.appusers.internal.dtos.CreateAppuserDto;
import dev.felix2000jp.springapplicationtemplate.appusers.internal.dtos.UpdateAppuserDto;
import dev.felix2000jp.springapplicationtemplate.shared.AuthorityValue;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.modulith.test.ApplicationModuleTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ApplicationModuleTest(extraIncludes = {"shared"}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AppuserControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Order(1)
    @Test
    void should_create_appuser() {
        var createAppuserDto = new CreateAppuserDto("username", "password");
        var response = restTemplate.postForEntity("/app/appusers", createAppuserDto, AppuserDto.class);

        var responseStatusCode = response.getStatusCode().value();
        var responseBody = response.getBody();

        assertThat(responseStatusCode).isEqualTo(201);
        assertThat(responseBody).isNotNull();
    }

    @Order(2)
    @Test
    void should_find_appuser() {
        var response = restTemplate
                .withBasicAuth("username", "password")
                .getForEntity("/app/appusers", AppuserDto.class);

        var responseStatusCode = response.getStatusCode().value();
        var responseBody = response.getBody();

        assertThat(responseStatusCode).isEqualTo(200);
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.username()).isEqualTo("username");
        assertThat(responseBody.authorities()).isEqualTo(List.of(AuthorityValue.APPLICATION));
    }

    @Order(3)
    @Test
    void should_generate_token_for_authentication() {
        var response = restTemplate
                .withBasicAuth("username", "password")
                .postForEntity("/app/appusers/token", null, String.class);

        var responseStatusCode = response.getStatusCode().value();
        var responseBody = response.getBody();

        assertThat(responseStatusCode).isEqualTo(200);
        assertThat(responseBody).isNotNull();
    }

    @Order(4)
    @Test
    void should_update_appuser() {
        var updateAppuserDto = new UpdateAppuserDto("new username", "new password");
        restTemplate
                .withBasicAuth("username", "password")
                .put("/app/appusers", updateAppuserDto);

        var response = restTemplate
                .withBasicAuth("new username", "new password")
                .getForEntity("/app/appusers", AppuserDto.class);

        var responseStatusCode = response.getStatusCode().value();
        var responseBody = response.getBody();

        assertThat(responseStatusCode).isEqualTo(200);
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.username()).isEqualTo("new username");
        assertThat(responseBody.authorities()).isEqualTo(List.of(AuthorityValue.APPLICATION));
    }

    @Order(5)
    @Test
    void should_delete_appuser() {
        restTemplate
                .withBasicAuth("new username", "new password")
                .delete("/app/appusers");

        var response = restTemplate
                .withBasicAuth("new username", "new password")
                .getForEntity("/app/appusers", AppuserDto.class);

        var responseStatus = response.getStatusCode().value();

        assertThat(responseStatus).isEqualTo(401);
    }

}
