package dev.felix2000jp.springapplicationtemplate.appusers.internal;

import dev.felix2000jp.springapplicationtemplate.appusers.internal.dtos.AppuserDTO;
import dev.felix2000jp.springapplicationtemplate.appusers.internal.dtos.CreateAppuserDTO;
import dev.felix2000jp.springapplicationtemplate.appusers.internal.dtos.UpdateAppuserDTO;
import dev.felix2000jp.springapplicationtemplate.appusers.internal.exceptions.AppuserConflictException;
import dev.felix2000jp.springapplicationtemplate.appusers.internal.exceptions.AppuserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AppuserController.class)
class AppuserControllerTest {

    @MockBean
    private AppuserService appuserService;
    @Autowired
    private MockMvc mockMvc;

    private AppuserDTO appuserDTO;
    private String appuserDTOJson;

    @BeforeEach
    void setUp() {
        appuserDTO = new AppuserDTO(UUID.randomUUID(), "username", Set.of("APPLICATION"));
        appuserDTOJson = String.format("""
                {
                    "id": "%s",
                    "username": "%s",
                    "authorities": %s
                }
                """, appuserDTO.id(), appuserDTO.username(), appuserDTO.authorities());
    }

    @Test
    @WithMockUser
    void find_should_return_ok_when_appuser_is_found() throws Exception {
        when(appuserService.find()).thenReturn(appuserDTO);

        mockMvc
                .perform(get("/api/appusers"))
                .andExpect(status().isOk())
                .andExpect(content().json(appuserDTOJson));
    }

    @Test
    @WithMockUser
    void find_should_return_not_found_when_appuser_is_not_found() throws Exception {
        when(appuserService.find()).thenThrow(new AppuserNotFoundException());

        mockMvc
                .perform(get("/api/appusers"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @WithMockUser
    void create_should_return_created_with_location_when_appuser_is_created() throws Exception {
        var createAppuserDTO = new CreateAppuserDTO("new username", "new password");
        var createAppuserDTOJson = String.format("""
                {
                    "username": "%s",
                    "password": "%s"
                }
                """, createAppuserDTO.username(), createAppuserDTO.password());

        when(appuserService.create(createAppuserDTO)).thenReturn(appuserDTO);

        mockMvc
                .perform(
                        post("/api/appusers")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(createAppuserDTOJson)
                )
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/app/appusers"))
                .andExpect(content().json(appuserDTOJson));
    }

    @Test
    @WithMockUser
    void create_should_return_conflict_when_appuser_already_exists() throws Exception {
        var createAppuserDTO = new CreateAppuserDTO("new username", "new password");
        var createAppuserDTOJson = String.format("""
                {
                    "username": "%s",
                    "password": "%s"
                }
                """, createAppuserDTO.username(), createAppuserDTO.password());

        when(appuserService.create(createAppuserDTO)).thenThrow(new AppuserConflictException());

        mockMvc
                .perform(
                        post("/api/appusers")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(createAppuserDTOJson)
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Conflict"))
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    @WithMockUser
    void update_should_return_no_content_when_appuser_updated() throws Exception {
        var updateAppuserDTO = new UpdateAppuserDTO("new username", "new password");
        var updateAppuserDTOJson = String.format("""
                {
                    "username": "%s",
                    "password": "%s"
                }
                """, updateAppuserDTO.username(), updateAppuserDTO.password());

        when(appuserService.update(updateAppuserDTO)).thenReturn(appuserDTO);

        mockMvc
                .perform(
                        put("/api/appusers")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updateAppuserDTOJson)
                )
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void update_should_return_not_found_when_appuser_not_found() throws Exception {
        var updateAppuserDTO = new UpdateAppuserDTO("new username", "new password");
        var updateAppuserDTOJson = String.format("""
                {
                    "username": "%s",
                    "password": "%s"
                }
                """, updateAppuserDTO.username(), updateAppuserDTO.password());

        when(appuserService.update(updateAppuserDTO)).thenThrow(new AppuserNotFoundException());

        mockMvc
                .perform(
                        put("/api/appusers")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updateAppuserDTOJson)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @WithMockUser
    void update_should_return_conflict_when_username_already_exists() throws Exception {
        var updateAppuserDTO = new UpdateAppuserDTO("new username", "new password");
        var updateAppuserDTOJson = String.format("""
                {
                    "username": "%s",
                    "password": "%s"
                }
                """, updateAppuserDTO.username(), updateAppuserDTO.password());

        when(appuserService.update(updateAppuserDTO)).thenThrow(new AppuserConflictException());

        mockMvc
                .perform(
                        put("/api/appusers")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updateAppuserDTOJson)
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Conflict"))
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    @WithMockUser
    void delete_should_return_no_content_when_appuser_is_deleted() throws Exception {
        when(appuserService.delete()).thenReturn(appuserDTO);

        mockMvc
                .perform(delete("/api/appusers").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void delete_should_return_not_found_when_appuser_is_not_found() throws Exception {
        when(appuserService.delete()).thenThrow(new AppuserNotFoundException());

        mockMvc
                .perform(delete("/api/appusers").with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.status").value(404));
    }

}
