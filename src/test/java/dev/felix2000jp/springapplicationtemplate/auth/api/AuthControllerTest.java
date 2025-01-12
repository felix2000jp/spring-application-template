package dev.felix2000jp.springapplicationtemplate.auth.api;

import dev.felix2000jp.springapplicationtemplate.auth.application.AuthService;
import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.CreateAppuserDTO;
import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.UpdatePasswordDTO;
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

import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class)
class AuthControllerTest {

    @MockitoBean
    private AuthService authService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser
    void should_return_200_and_csrf_token_successfully() throws Exception {
        mockMvc
                .perform(get("/auth/csrf"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(emptyString())));
    }

    @Test
    @WithMockUser
    void should_return_200_and_login_token_successfully() throws Exception {
        when(authService.generateToken()).thenReturn("some-login-token");

        mockMvc
                .perform(post("/auth/login").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("some-login-token"));
    }

    @Test
    @WithMockUser
    void should_return_201_and_location_when_user_is_registered() throws Exception {
        var createAppuserDTO = new CreateAppuserDTO("username", "password");

        var requestBody = String.format("""
                { "username": "%s", "password": "%s" }
                """, createAppuserDTO.username(), createAppuserDTO.password());

        mockMvc
                .perform(post("/auth/register").with(csrf()).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(header().string("LOCATION", "/api/appusers/me"));
    }

    @ParameterizedTest
    @MethodSource
    @WithMockUser
    void should_return_400_when_create_appuser_request_body_is_invalid(String requestBody) throws Exception {
        mockMvc
                .perform(post("/auth/register").with(csrf()).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @WithMockUser
    void should_return_204_when_user_password_is_updated() throws Exception {
        var updatePasswordDTO = new UpdatePasswordDTO("password");

        var requestBody = String.format("""
                { "password": "%s" }
                """, updatePasswordDTO.password());

        mockMvc
                .perform(put("/auth/password").with(csrf()).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isNoContent());
    }

    @ParameterizedTest
    @MethodSource
    @WithMockUser
    void should_return_400_when_update_password_request_body_is_invalid(String requestBody) throws Exception {
        mockMvc
                .perform(put("/auth/password").with(csrf()).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400));
    }

    private static Stream<Arguments> should_return_400_when_create_appuser_request_body_is_invalid() {
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

    private static Stream<Arguments> should_return_400_when_update_password_request_body_is_invalid() {
        return Stream.of(
                arguments(""),
                arguments("{}"),
                arguments("{ 'password': null }"),
                arguments("{ 'password': '' }"),
                arguments("{ 'password': ' ' }"),
                arguments("{ 'password': 'lol' }"),
                arguments("{ 'password': '" + "a".repeat(501) + "' }")
        );
    }

}
