package dev.felix2000jp.springapplicationtemplate.auth.api;

import dev.felix2000jp.springapplicationtemplate.auth.application.AppuserService;
import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.AppuserDTO;
import dev.felix2000jp.springapplicationtemplate.auth.application.dtos.AppuserListDTO;
import dev.felix2000jp.springapplicationtemplate.shared.SecurityService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = AppuserController.class)
class AppuserControllerTest {

    @MockitoBean
    private AppuserService appuserService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser
    void should_return_200_and_a_page_of_appusers_when_page_of_appusers_is_valid() throws Exception {
        var appuserDTO = new AppuserDTO(
                UUID.randomUUID(),
                "username",
                Set.of(SecurityService.Scope.APPLICATION.name())
        );
        var appuserListDTO = new AppuserListDTO(List.of(appuserDTO));

        var expectedResponse = String.format("""
                {
                    "appusers": [{ "id": "%s", "username": "%s", "scopes": ["%s"] }]
                }
                """, appuserDTO.id(), appuserDTO.username(), appuserDTO.scopes().iterator().next());

        when(appuserService.getAll(0)).thenReturn(appuserListDTO);

        mockMvc
                .perform(get("/api/appusers/admin?page=0"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    @WithMockUser
    void should_return_200_and_page_0_of_appusers_when_page_param_is_missing() throws Exception {
        var appuserDTO = new AppuserDTO(
                UUID.randomUUID(),
                "username",
                Set.of(SecurityService.Scope.APPLICATION.name())
        );
        var appuserListDTO = new AppuserListDTO(List.of(appuserDTO));

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

}