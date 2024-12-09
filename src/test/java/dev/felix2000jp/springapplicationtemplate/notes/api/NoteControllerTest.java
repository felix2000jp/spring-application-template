package dev.felix2000jp.springapplicationtemplate.notes.api;

import dev.felix2000jp.springapplicationtemplate.notes.application.NoteService;
import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.NoteDTO;
import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.NoteListDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = NoteController.class)
class NoteControllerTest {

    @MockitoBean
    private NoteService noteService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser
    void should_get_page_of_notes_from_the_logged_in_user() throws Exception {
        var noteDTO = new NoteDTO(UUID.randomUUID(), "title", "content");
        var noteListDTO = new NoteListDTO(List.of(noteDTO));

        var expectedResponse = String.format("""
                {
                    "notes": [{ "id": "%s", "title": "%s", "content": "%s" }]
                }
                """, noteDTO.id(), noteDTO.title(), noteDTO.content());

        when(noteService.getByAppuser(0)).thenReturn(noteListDTO);

        mockMvc
                .perform(get("/api/notes?page=0"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    @WithMockUser
    void should_fail_with_400_when_getting_page_of_notes_from_the_logged_in_user_with_page_missing() throws Exception {
        mockMvc
                .perform(get("/api/notes"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "-1", "not a number"})
    @WithMockUser
    void should_fail_with_400_when_getting_page_of_notes_from_the_logged_in_user_with_invalid_page(String page) throws Exception {
        mockMvc
                .perform(get("/api/notes?page=" + page))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400));
    }

}
