package dev.felix2000jp.springapplicationtemplate.auth.api;

import dev.felix2000jp.springapplicationtemplate.auth.application.AppuserService;
import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.AppuserDto;
import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.AppuserListDto;
import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.UpdateAppuserDto;
import dev.felix2000jp.springapplicationtemplate.auth.domain.exceptions.AppuserNotFoundException;
import dev.felix2000jp.springapplicationtemplate.shared.SecurityService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
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

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AppuserController.class)
class AppuserControllerTest {

    @MockitoBean
    private AppuserService appuserService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser
    void should_return_200_and_a_page_of_appusers_when_page_param_is_valid() throws Exception {
        var appuserDTO = new AppuserDto(
                UUID.randomUUID(),
                "username",
                Set.of(SecurityService.Scope.APPLICATION.name())
        );
        var appuserListDTO = new AppuserListDto(List.of(appuserDTO));

        var expectedResponse = String.format("""
                {
                    "appusers": [{ "id": "%s", "username": "%s", "scopes": ["%s"] }]
                }
                """, appuserDTO.id(), appuserDTO.username(), appuserDTO.scopes().iterator().next());

        when(appuserService.getAll(0)).thenReturn(appuserListDTO);

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
    void should_fail_to_get_page_of_appusers_when_page_query_param_is_invalid(String page) throws Exception {
        mockMvc
                .perform(get("/api/appusers/admin?page=" + page))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @WithMockUser
    void should_return_200_and_the_current_appuser_when_appuser_exists() throws Exception {
        var appuserDTO = new AppuserDto(
                UUID.randomUUID(),
                "username",
                Set.of(SecurityService.Scope.APPLICATION.name())
        );

        var expectedResponse = String.format("""
                { "id": "%s", "username": "%s", "scopes": ["%s"] }
                """, appuserDTO.id(), appuserDTO.username(), appuserDTO.scopes().iterator().next());

        when(appuserService.getByCurrentUser()).thenReturn(appuserDTO);

        mockMvc
                .perform(get("/api/appusers/me"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    @WithMockUser
    void should_fail_to_get_current_appuser_and_return_404_when_appuser_does_not_exist() throws Exception {
        var exception = new AppuserNotFoundException();
        when(appuserService.getByCurrentUser()).thenThrow(exception);

        mockMvc
                .perform(get("/api/appusers/me"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.detail").value(exception.getMessage()))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @WithMockUser
    void should_return_204_and_the_updated_appuser_when_appuser_exists_and_new_username_is_unique() throws Exception {
        var updateAppuserDTO = new UpdateAppuserDto("new username");
        var appuserDTO = new AppuserDto(
                UUID.randomUUID(),
                updateAppuserDTO.username(),
                Set.of(SecurityService.Scope.APPLICATION.name())
        );

        var requestBody = String.format("""
                { "username": "%s" }
                """, updateAppuserDTO.username());

        when(appuserService.updateByCurrentUser(updateAppuserDTO)).thenReturn(appuserDTO);

        mockMvc
                .perform(put("/api/appusers/me").with(csrf()).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void should_fail_to_update_appuser_and_return_404_when_appuser_does_not_exist() throws Exception {
        var updateAppuserDTO = new UpdateAppuserDto("new username");

        var requestBody = String.format("""
                { "username": "%s" }
                """, updateAppuserDTO.username());

        var exception = new AppuserNotFoundException();
        when(appuserService.updateByCurrentUser(updateAppuserDTO)).thenThrow(exception);

        mockMvc
                .perform(put("/api/appusers/me").with(csrf()).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.detail").value(exception.getMessage()))
                .andExpect(jsonPath("$.status").value(404));
    }

}