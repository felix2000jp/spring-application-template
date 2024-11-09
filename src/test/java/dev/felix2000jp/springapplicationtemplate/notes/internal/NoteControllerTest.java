package dev.felix2000jp.springapplicationtemplate.notes.internal;

import dev.felix2000jp.springapplicationtemplate.notes.internal.dtos.CreateNoteDTO;
import dev.felix2000jp.springapplicationtemplate.notes.internal.dtos.NoteDTO;
import dev.felix2000jp.springapplicationtemplate.notes.internal.dtos.NoteListDTO;
import dev.felix2000jp.springapplicationtemplate.notes.internal.dtos.UpdateNoteDTO;
import dev.felix2000jp.springapplicationtemplate.notes.internal.exceptions.NoteNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = NoteController.class)
class NoteControllerTest {

    @MockBean
    private NoteService noteService;
    @Autowired
    private MockMvc mockMvc;

    private NoteDTO noteDTO;
    private String noteDTOJson;

    @BeforeEach
    void setUp() {
        noteDTO = new NoteDTO(UUID.randomUUID(), "title", "content");
        noteDTOJson = String.format("""
                {
                    "id": "%s",
                    "title": "%s",
                    "content": "%s"
                }
                """, noteDTO.id(), noteDTO.title(), noteDTO.content());
    }

    @Test
    @WithMockUser
    void findAll_should_return_ok_when_notes_are_found() throws Exception {
        var noteListDTO = new NoteListDTO(List.of(noteDTO));
        when(noteService.findAll()).thenReturn(noteListDTO);

        var expected = String.format("""
                {
                    "notes": [ %s ]
                }
                """, noteDTOJson);

        mockMvc
                .perform(get("/api/notes"))
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    @Test
    @WithMockUser
    void findAll_should_return_ok_when_notes_are_not_found() throws Exception {
        var noteListDTO = new NoteListDTO(List.of());
        when(noteService.findAll()).thenReturn(noteListDTO);

        var expected = " { \"notes\": [] } ";

        mockMvc
                .perform(get("/api/notes"))
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    @Test
    @WithMockUser
    void findById_should_return_ok_when_note_is_found() throws Exception {
        when(noteService.find(noteDTO.id())).thenReturn(noteDTO);

        mockMvc
                .perform(get("/api/notes/{id}", noteDTO.id()))
                .andExpect(status().isOk())
                .andExpect(content().json(noteDTOJson));
    }

    @Test
    @WithMockUser
    void findById_should_return_not_found_when_note_is_not_found() throws Exception {
        when(noteService.find(noteDTO.id())).thenThrow(new NoteNotFoundException());

        mockMvc
                .perform(get("/api/notes/{id}", noteDTO.id()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @WithMockUser
    void create_should_return_created_when_note_is_created() throws Exception {
        var createdNoteDTO = new CreateNoteDTO(noteDTO.title(), noteDTO.content());
        var createNotDTOJson = String.format("""
                {
                    "title": "%s",
                    "content": "%s"
                }
                """, createdNoteDTO.title(), createdNoteDTO.content());

        when(noteService.create((createdNoteDTO))).thenReturn(noteDTO);

        mockMvc
                .perform(
                        post("/api/notes")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(createNotDTOJson)
                )
                .andExpect(status().isCreated())
                .andExpect(content().json(noteDTOJson));
    }

    @Test
    @WithMockUser
    void update_should_return_no_content_when_note_is_updated() throws Exception {
        var updateNoteDTO = new UpdateNoteDTO(noteDTO.title(), noteDTO.content());
        var updateNoteDTOJson = String.format("""
                {
                    "title": "%s",
                    "content": "%s"
                }
                """, updateNoteDTO.title(), updateNoteDTO.content());

        when(noteService.update(noteDTO.id(), updateNoteDTO)).thenReturn(noteDTO);

        mockMvc
                .perform(
                        put("/api/notes/{id}", noteDTO.id())
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updateNoteDTOJson)
                )
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void update_should_return_not_found_when_note_is_not_found() throws Exception {
        var updateNoteDTO = new UpdateNoteDTO(noteDTO.title(), noteDTO.content());
        var updateNoteDTOJson = String.format("""
                {
                    "title": "%s",
                    "content": "%s"
                }
                """, updateNoteDTO.title(), updateNoteDTO.content());

        when(noteService.update(noteDTO.id(), updateNoteDTO)).thenThrow(new NoteNotFoundException());

        mockMvc
                .perform(
                        put("/api/notes/{id}", noteDTO.id())
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updateNoteDTOJson)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @WithMockUser
    void delete_should_return_no_content_when_note_is_deleted() throws Exception {
        when(noteService.delete(noteDTO.id())).thenReturn(noteDTO);

        mockMvc
                .perform(delete("/api/notes/{id}", noteDTO.id()).with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void delete_should_return_not_found_when_appuser_is_not_found() throws Exception {
        when(noteService.delete(noteDTO.id())).thenThrow(new NoteNotFoundException());

        mockMvc
                .perform(delete("/api/notes/{id}", noteDTO.id()).with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.status").value(404));
    }
}
