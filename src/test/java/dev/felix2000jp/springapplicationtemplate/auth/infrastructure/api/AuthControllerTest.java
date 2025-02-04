package dev.felix2000jp.springapplicationtemplate.auth.infrastructure.api;

import dev.felix2000jp.springapplicationtemplate.auth.application.AuthService;
import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.CreateAppuserDto;
import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.UpdateAppuserDto;
import dev.felix2000jp.springapplicationtemplate.auth.domain.exceptions.AppuserAlreadyExistsException;
import dev.felix2000jp.springapplicationtemplate.auth.domain.exceptions.AppuserNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class)
class AuthControllerTest {

    @MockitoBean
    private AuthService authService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser
    void generateToken_then_return_200_and_login_token() throws Exception {
        when(authService.generateToken()).thenReturn("some-login-token");

        mockMvc
                .perform(post("/auth/login").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("some-login-token"));
    }

    @Test
    @WithMockUser
    void createAppuser_given_valid_body_then_return_201_and_location_header() throws Exception {
        var createAppuserDto = new CreateAppuserDto("username", "password");

        var requestBody = String.format("""
                { "username": "%s", "password": "%s" }
                """, createAppuserDto.username(), createAppuserDto.password());

        mockMvc
                .perform(post("/auth").with(csrf()).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(header().string("LOCATION", "/api/appusers/me"));
    }

    @Test
    @WithMockUser
    void createAppuser_given_duplicate_username_then_return_409() throws Exception {
        var createAppuserDto = new CreateAppuserDto("username", "password");

        var requestBody = String.format("""
                { "username": "%s", "password": "%s" }
                """, createAppuserDto.username(), createAppuserDto.password());

        var exception = new AppuserAlreadyExistsException();
        doThrow(exception).when(authService).createAppuser(createAppuserDto);

        mockMvc
                .perform(post("/auth").with(csrf()).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Conflict"))
                .andExpect(jsonPath("$.detail").value(exception.getMessage()))
                .andExpect(jsonPath("$.status").value(409));
    }

    @ParameterizedTest
    @MethodSource
    @WithMockUser
    void createAppuser_given_invalid_request_body_then_return_400(String requestBody) throws Exception {
        mockMvc
                .perform(post("/auth").with(csrf()).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @WithMockUser
    void updateAppuser_given_valid_body_then_return_204() throws Exception {
        var updateAppuserDto = new UpdateAppuserDto("new username","new password");

        var requestBody = String.format("""
                { "username": "%s", "password": "%s" }
                """, updateAppuserDto.username(), updateAppuserDto.password());

        mockMvc
                .perform(put("/auth").with(csrf()).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void updateAppuser_give_non_existing_user_then_return_404() throws Exception {
        var updateAppuserDto = new UpdateAppuserDto("new username","new password");

        var requestBody = String.format("""
                { "username": "%s", "password": "%s" }
                """, updateAppuserDto.username(), updateAppuserDto.password());

        var exception = new AppuserNotFoundException();
        doThrow(exception).when(authService).updateAppuser(updateAppuserDto);

        mockMvc
                .perform(put("/auth").with(csrf()).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.detail").value(exception.getMessage()))
                .andExpect(jsonPath("$.status").value(404));
    }

    @ParameterizedTest
    @MethodSource
    @WithMockUser
    void updateAppuser_given_invalid_request_body_then_return_400(String requestBody) throws Exception {
        mockMvc
                .perform(put("/auth").with(csrf()).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @WithMockUser
    void deleteAppuser_given_user_then_return_204() throws Exception {
        mockMvc
                .perform(delete("/auth").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void deleteAppuser_given_not_found_user_then_return_404() throws Exception {
        var exception = new AppuserNotFoundException();
        doThrow(exception).when(authService).deleteAppuser();

        mockMvc
                .perform(delete("/auth").with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.detail").value(exception.getMessage()))
                .andExpect(jsonPath("$.status").value(404));
    }


    private static Stream<Arguments> createAppuser_given_invalid_request_body_then_return_400() {
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
