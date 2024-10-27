package dev.felix2000jp.springapplicationtemplate.appusers.internal;

import dev.felix2000jp.springapplicationtemplate.appusers.internal.dtos.AppuserDto;
import dev.felix2000jp.springapplicationtemplate.appusers.internal.dtos.CreateAppuserDto;
import dev.felix2000jp.springapplicationtemplate.appusers.internal.dtos.UpdateAppuserDto;
import dev.felix2000jp.springapplicationtemplate.appusers.internal.exceptions.AppuserConflictException;
import dev.felix2000jp.springapplicationtemplate.appusers.internal.exceptions.AppuserNotFoundException;
import dev.felix2000jp.springapplicationtemplate.shared.AuthorityValue;
import dev.felix2000jp.springapplicationtemplate.shared.security.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(SecurityConfig.class)
@WebMvcTest(controllers = {AppuserController.class})
class AppuserControllerTest {

    @MockBean
    private AppuserService appuserService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private MockMvc mockMvc;

    private AppuserDto appuserDto;
    private String appuserDtoJson;

    @BeforeEach
    void setUp() {
        var encodedPassword = passwordEncoder.encode("password");
        var appuser = new Appuser(UUID.randomUUID(), "username", encodedPassword, AuthorityValue.APPLICATION);

        when(appuserService.loadUserByUsername("username")).thenReturn(appuser);

        appuserDto = new AppuserDto(appuser.getId(), appuser.getUsername(), appuser.getAuthorityValues());
        appuserDtoJson = String.format("""
                {
                    "id": "%s",
                    "username": "%s",
                    "authorities": %s
                }
                """, appuserDto.id(), appuserDto.username(), appuserDto.authorities());
    }

    @Test
    void find_should_return_ok_when_appuser_is_found() throws Exception {
        when(appuserService.find(any(Appuser.class))).thenReturn(appuserDto);

        mockMvc
                .perform(get("/app/appusers").with(httpBasic("username", "password")))
                .andExpect(status().isOk())
                .andExpect(content().json(appuserDtoJson));
    }

    @Test
    void find_should_return_not_found_when_appuser_is_not_found() throws Exception {
        when(appuserService.find(any(Appuser.class))).thenThrow(new AppuserNotFoundException());

        mockMvc
                .perform(get("/app/appusers").with(httpBasic("username", "password")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void find_should_return_unauthorized_when_credentials_are_wrong() throws Exception {
        mockMvc
                .perform(get("/app/appusers").with(httpBasic("username", "wrong password")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void create_should_return_created_with_location_when_appuser_is_created() throws Exception {
        var createAppuserDto = new CreateAppuserDto("new username", "new password");
        var createAppuserDtoJson = String.format("""
                {
                    "username": "%s",
                    "password": "%s"
                }
                """, createAppuserDto.username(), createAppuserDto.password());

        when(appuserService.create(eq(createAppuserDto))).thenReturn(appuserDto);

        mockMvc
                .perform(post("/app/appusers").contentType(MediaType.APPLICATION_JSON).content(createAppuserDtoJson))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/app/appusers"))
                .andExpect(content().json(appuserDtoJson));
    }

    @Test
    void create_should_return_conflict_when_appuser_already_exists() throws Exception {
        var createAppuserDto = new CreateAppuserDto("new username", "new password");
        var createAppuserDtoJson = String.format("""
                {
                    "username": "%s",
                    "password": "%s"
                }
                """, createAppuserDto.username(), createAppuserDto.password());

        when(appuserService.create(eq(createAppuserDto))).thenThrow(new AppuserConflictException());

        mockMvc
                .perform(post("/app/appusers").contentType(MediaType.APPLICATION_JSON).content(createAppuserDtoJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Conflict"))
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void update_should_return_no_content_when_appuser_updated() throws Exception {
        var updateAppuserDto = new UpdateAppuserDto("new username", "new password");
        var updateAppuserDtoJson = String.format("""
                {
                    "username": "%s",
                    "password": "%s"
                }
                """, updateAppuserDto.username(), updateAppuserDto.password());

        when(appuserService.update(any(Appuser.class), eq(updateAppuserDto))).thenReturn(appuserDto);

        mockMvc
                .perform(
                        put("/app/appusers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updateAppuserDtoJson)
                                .with(httpBasic("username", "password"))
                )
                .andExpect(status().isNoContent());
    }

    @Test
    void update_should_return_not_found_when_appuser_not_found() throws Exception {
        var updateAppuserDto = new UpdateAppuserDto("new username", "new password");
        var updateAppuserDtoJson = String.format("""
                {
                    "username": "%s",
                    "password": "%s"
                }
                """, updateAppuserDto.username(), updateAppuserDto.password());

        when(appuserService.update(any(Appuser.class), eq(updateAppuserDto))).thenThrow(new AppuserNotFoundException());

        mockMvc
                .perform(
                        put("/app/appusers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updateAppuserDtoJson)
                                .with(httpBasic("username", "password"))
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void update_should_return_conflict_when_username_already_exists() throws Exception {
        var updateAppuserDto = new UpdateAppuserDto("new username", "new password");
        var updateAppuserDtoJson = String.format("""
                {
                    "username": "%s",
                    "password": "%s"
                }
                """, updateAppuserDto.username(), updateAppuserDto.password());

        when(appuserService.update(any(Appuser.class), eq(updateAppuserDto))).thenThrow(new AppuserConflictException());

        mockMvc
                .perform(
                        put("/app/appusers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updateAppuserDtoJson)
                                .with(httpBasic("username", "password"))
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Conflict"))
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void update_should_return_unauthorized_when_credentials_are_wrong() throws Exception {
        var updateAppuserDto = new UpdateAppuserDto("new username", "new password");
        var updateAppuserDtoJson = String.format("""
                {
                    "username": "%s",
                    "password": "%s"
                }
                """, updateAppuserDto.username(), updateAppuserDto.password());

        mockMvc
                .perform(
                        put("/app/appusers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updateAppuserDtoJson)
                                .with(httpBasic("username", "wrong password"))
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void delete_should_return_no_content_when_appuser_is_deleted() throws Exception {
        when(appuserService.delete(any(Appuser.class))).thenReturn(appuserDto);

        mockMvc
                .perform(delete("/app/appusers").with(httpBasic("username", "password")))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_should_return_not_found_when_appuser_is_not_found() throws Exception {
        when(appuserService.delete(any(Appuser.class))).thenThrow(new AppuserNotFoundException());

        mockMvc
                .perform(delete("/app/appusers").with(httpBasic("username", "password")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void delete_should_return_unauthorized_when_credentials_are_wrong() throws Exception {
        mockMvc
                .perform(delete("/app/appusers").with(httpBasic("username", "wrong password")))
                .andExpect(status().isUnauthorized());
    }

}
