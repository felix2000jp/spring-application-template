package dev.felix2000jp.springapplicationtemplate.auth.infrastructure.api;

import dev.felix2000jp.springapplicationtemplate.auth.application.AppuserService;
import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.AppuserDto;
import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.AppuserListDto;
import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.UpdateAppuserDto;
import dev.felix2000jp.springapplicationtemplate.auth.domain.exceptions.AppuserAlreadyExistsException;
import dev.felix2000jp.springapplicationtemplate.auth.domain.exceptions.AppuserNotFoundException;
import dev.felix2000jp.springapplicationtemplate.shared.security.SecurityScope;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AppuserController.class)
class AppuserControllerTest {

    @MockitoBean
    private AppuserService appuserService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser
    void getAppusers_given_valid_page_param_then_return_200_and_a_page_of_appusers() throws Exception {
        var appuserDto = new AppuserDto(
                UUID.randomUUID(),
                "username",
                Set.of(SecurityScope.APPLICATION.name())
        );
        var appuserListDto = new AppuserListDto(List.of(appuserDto));

        var expectedResponse = String.format("""
                {
                    "appusers": [{ "id": "%s", "username": "%s", "scopes": ["%s"] }]
                }
                """, appuserDto.id(), appuserDto.username(), appuserDto.scopes().iterator().next());

        when(appuserService.getAppusers(0)).thenReturn(appuserListDto);

        mockMvc
                .perform(get("/api/appusers/admin"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));

        mockMvc
                .perform(get("/api/appusers/admin?page=0"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @ParameterizedTest
    @ValueSource(strings = {"-1", "not a number"})
    @WithMockUser
    void getAppusers_given_invalid_page_param_then_return_400(String page) throws Exception {
        mockMvc
                .perform(get("/api/appusers/admin?page=" + page))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @WithMockUser
    void getAppuserForCurrentUser_given_user_then_return_200_and_the_current_appuser() throws Exception {
        var appuserDto = new AppuserDto(
                UUID.randomUUID(),
                "username",
                Set.of(SecurityScope.APPLICATION.name())
        );

        var expectedResponse = String.format("""
                { "id": "%s", "username": "%s", "scopes": ["%s"] }
                """, appuserDto.id(), appuserDto.username(), appuserDto.scopes().iterator().next());

        when(appuserService.getAppuserForCurrentUser()).thenReturn(appuserDto);

        mockMvc
                .perform(get("/api/appusers"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    @WithMockUser
    void getAppuserForCurrentUser_given_not_found_user_then_return_404() throws Exception {
        var exception = new AppuserNotFoundException();
        when(appuserService.getAppuserForCurrentUser()).thenThrow(exception);

        mockMvc
                .perform(get("/api/appusers"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.detail").value(exception.getMessage()))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @WithMockUser
    void updateAppuser_given_valid_body_then_return_204() throws Exception {
        var updateAppuserDto = new UpdateAppuserDto("new username", "new password");

        var requestBody = String.format("""
                { "username": "%s", "password": "%s" }
                """, updateAppuserDto.username(), updateAppuserDto.password());

        mockMvc
                .perform(put("/api/appusers").with(csrf()).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void updateAppuser_given_duplicate_username_then_return_409() throws Exception {
        var updateAppuserDto = new UpdateAppuserDto("new username", "new password");

        var requestBody = String.format("""
                { "username": "%s", "password": "%s" }
                """, updateAppuserDto.username(), updateAppuserDto.password());

        var exception = new AppuserAlreadyExistsException();
        doThrow(exception).when(appuserService).updateAppuser(updateAppuserDto);

        mockMvc
                .perform(put("/api/appusers").with(csrf()).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Conflict"))
                .andExpect(jsonPath("$.detail").value(exception.getMessage()))
                .andExpect(jsonPath("$.status").value(409));
    }

    @ParameterizedTest
    @MethodSource
    @WithMockUser
    void updateAppuser_given_invalid_request_body_then_return_400(String requestBody) throws Exception {
        mockMvc
                .perform(put("/api/appusers").with(csrf()).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @WithMockUser
    void deleteAppuser_given_user_then_return_204() throws Exception {
        mockMvc
                .perform(delete("/api/appusers").with(csrf()))
                .andExpect(status().isNoContent());
    }

    private static Stream<Arguments> updateAppuser_given_invalid_request_body_then_return_400() {
        return Stream.of(
                arguments(""),
                arguments("{}"),
                arguments("{ 'username': 'username' }"),
                arguments("{ 'password': 'password' }"),
                arguments("{ 'username': null, 'password': 'password' }"),
                arguments("{ 'username': 'username', 'password': null }"),
                arguments("{ 'username': '', 'password': 'password' }"),
                arguments("{ 'username': 'username', 'password': '' }"),
                arguments("{ 'username': ' ', 'password': 'password' }"),
                arguments("{ 'username': 'username', 'password': ' ' }"),
                arguments("{ 'username': 'lol', 'password': 'password' }"),
                arguments("{ 'username': 'username', 'password': 'lol' }"),
                arguments("{ 'username': '" + "a".repeat(501) + "', 'password': 'password' }"),
                arguments("{ 'username': 'username', 'password': '" + "a".repeat(501) + "' }")
        );
    }

}