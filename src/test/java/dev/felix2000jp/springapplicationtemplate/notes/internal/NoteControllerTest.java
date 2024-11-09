package dev.felix2000jp.springapplicationtemplate.notes.internal;

import dev.felix2000jp.springapplicationtemplate.appusers.internal.Appuser;
import dev.felix2000jp.springapplicationtemplate.appusers.internal.AppuserController;
import dev.felix2000jp.springapplicationtemplate.appusers.internal.AppuserService;
import dev.felix2000jp.springapplicationtemplate.notes.internal.dtos.CreateNoteDto;
import dev.felix2000jp.springapplicationtemplate.notes.internal.dtos.NoteDto;
import dev.felix2000jp.springapplicationtemplate.notes.internal.dtos.NoteListDto;
import dev.felix2000jp.springapplicationtemplate.notes.internal.dtos.UpdateNoteDto;
import dev.felix2000jp.springapplicationtemplate.notes.internal.exceptions.NoteNotFoundException;
import dev.felix2000jp.springapplicationtemplate.shared.AppuserPrincipal;
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

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import({SecurityConfig.class})
@WebMvcTest(controllers = {AppuserController.class, NoteController.class})
class NoteControllerTest {

    @MockBean
    private AppuserService appuserService;
    @MockBean
    private NoteService noteService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private MockMvc mockMvc;

    private NoteDto noteDto;
    private String noteDtoJson;
    private String authorizationHeaderValue;

    @BeforeEach
    void setUp() throws Exception {
        var encodedPassword = passwordEncoder.encode("password");
        var appuser = new Appuser(UUID.randomUUID(), "username", encodedPassword, AuthorityValue.APPLICATION);
        when(appuserService.loadUserByUsername(appuser.getUsername())).thenReturn(appuser);

        authorizationHeaderValue = "Bearer " + mockMvc
                .perform(post("/app/appusers/token").with(httpBasic("username", "password")))
                .andReturn()
                .getResponse()
                .getContentAsString();

        noteDto = new NoteDto(UUID.randomUUID(), "title", "content");
        noteDtoJson = String.format("""
                {
                    "id": "%s",
                    "title": "%s",
                    "content": "%s"
                }
                """, noteDto.id(), noteDto.title(), noteDto.content());
    }

    @Test
    void findAll_should_return_ok_when_notes_are_found() throws Exception {
        var noteListDto = new NoteListDto(List.of(noteDto));
        when(noteService.findAll(any(AppuserPrincipal.class))).thenReturn(noteListDto);

        var expected = String.format("""
                {
                    "notes": [ %s ]
                }
                """, noteDtoJson);

        mockMvc
                .perform(get("/api/notes").header("Authorization", authorizationHeaderValue))
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    @Test
    void findAll_should_return_ok_when_notes_are_not_found() throws Exception {
        var noteListDto = new NoteListDto(List.of());
        when(noteService.findAll(any(AppuserPrincipal.class))).thenReturn(noteListDto);

        var expected = " { \"notes\": [] } ";

        mockMvc
                .perform(get("/api/notes").header("Authorization", authorizationHeaderValue))
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    @Test
    void findAll_should_return_unauthorized_when_token_is_missing() throws Exception {
        mockMvc
                .perform(get("/api/notes"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void findById_should_return_ok_when_note_is_found() throws Exception {
        when(noteService.findById(any(AppuserPrincipal.class), eq(noteDto.id()))).thenReturn(noteDto);

        mockMvc
                .perform(get("/api/notes/{id}", noteDto.id()).header("Authorization", authorizationHeaderValue))
                .andExpect(status().isOk())
                .andExpect(content().json(noteDtoJson));
    }

    @Test
    void findById_should_return_not_found_when_note_is_not_found() throws Exception {
        when(noteService.findById(any(AppuserPrincipal.class), eq(noteDto.id()))).thenThrow(new NoteNotFoundException());

        mockMvc
                .perform(get("/api/notes/{id}", noteDto.id()).header("Authorization", authorizationHeaderValue))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void findById_should_return_unauthorized_when_token_is_missing() throws Exception {
        mockMvc
                .perform(get("/api/notes/{id}", noteDto.id()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void create_should_return_created_when_note_is_created() throws Exception {
        var createdNoteDto = new CreateNoteDto(noteDto.title(), noteDto.content());
        var createNotDtoJson = String.format("""
                {
                    "title": "%s",
                    "content": "%s"
                }
                """, createdNoteDto.title(), createdNoteDto.content());

        when(noteService.create(any(AppuserPrincipal.class), eq(createdNoteDto))).thenReturn(noteDto);

        mockMvc
                .perform(post("/api/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createNotDtoJson)
                        .header("Authorization", authorizationHeaderValue))
                .andExpect(status().isCreated())
                .andExpect(content().json(noteDtoJson));
    }

    @Test
    void create_should_return_unauthorized_when_token_is_missing() throws Exception {
        var createdNoteDto = new CreateNoteDto(noteDto.title(), noteDto.content());
        var createNotDtoJson = String.format("""
                {
                    "title": "%s",
                    "content": "%s"
                }
                """, createdNoteDto.title(), createdNoteDto.content());

        mockMvc
                .perform(post("/api/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createNotDtoJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void update_should_return_no_content_when_note_is_updated() throws Exception {
        var updateNoteDto = new UpdateNoteDto(noteDto.title(), noteDto.content());
        var updateNoteDtoJson = String.format("""
                {
                    "title": "%s",
                    "content": "%s"
                }
                """, updateNoteDto.title(), updateNoteDto.content());

        when(noteService.update(any(AppuserPrincipal.class), eq(noteDto.id()), eq(updateNoteDto))).thenReturn(noteDto);

        mockMvc
                .perform(put("/api/notes/{id}", noteDto.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateNoteDtoJson)
                        .header("Authorization", authorizationHeaderValue))
                .andExpect(status().isNoContent());
    }

    @Test
    void update_should_return_not_found_when_note_is_not_found() throws Exception {
        var updateNoteDto = new UpdateNoteDto(noteDto.title(), noteDto.content());
        var updateNoteDtoJson = String.format("""
                {
                    "title": "%s",
                    "content": "%s"
                }
                """, updateNoteDto.title(), updateNoteDto.content());

        when(
                noteService.update(any(AppuserPrincipal.class), eq(noteDto.id()), eq(updateNoteDto))
        ).thenThrow(new NoteNotFoundException());

        mockMvc
                .perform(put("/api/notes/{id}", noteDto.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateNoteDtoJson)
                        .header("Authorization", authorizationHeaderValue))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void update_should_return_unauthorized_when_token_is_missing() throws Exception {
        var updateNoteDto = new UpdateNoteDto(noteDto.title(), noteDto.content());
        var updateNoteDtoJson = String.format("""
                {
                    "title": "%s",
                    "content": "%s"
                }
                """, updateNoteDto.title(), updateNoteDto.content());

        mockMvc
                .perform(put("/api/notes/{id}", noteDto.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateNoteDtoJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void delete_should_return_no_content_when_note_is_deleted() throws Exception {
        when(noteService.delete(any(AppuserPrincipal.class), eq(noteDto.id()))).thenReturn(noteDto);

        mockMvc
                .perform(delete("/api/notes/{id}", noteDto.id()).header("Authorization", authorizationHeaderValue))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_should_return_not_found_when_appuser_is_not_found() throws Exception {
        when(noteService.delete(any(AppuserPrincipal.class), eq(noteDto.id()))).thenThrow(new NoteNotFoundException());

        mockMvc
                .perform(delete("/api/notes/{id}", noteDto.id()).header("Authorization", authorizationHeaderValue))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void delete_should_return_unauthorized_when_token_is_invalid() throws Exception {
        mockMvc
                .perform(delete("/api/notes/{id}", noteDto.id()))
                .andExpect(status().isUnauthorized());
    }
}
