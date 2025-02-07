package dev.felix2000jp.springapplicationtemplate.auth.infrastructure.api;

import dev.felix2000jp.springapplicationtemplate.auth.application.AppuserService;
import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.AppuserDto;
import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.AppuserListDto;
import dev.felix2000jp.springapplicationtemplate.auth.domain.exceptions.AppuserNotFoundException;
import dev.felix2000jp.springapplicationtemplate.shared.security.SecurityService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
                .perform(get("/api/appusers/me"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    @WithMockUser
    void getAppuserForCurrentUser_given_not_found_user_then_return_404() throws Exception {
        var exception = new AppuserNotFoundException();
        when(appuserService.getAppuserForCurrentUser()).thenThrow(exception);

        mockMvc
                .perform(get("/api/appusers/me"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.detail").value(exception.getMessage()))
                .andExpect(jsonPath("$.status").value(404));
    }

}