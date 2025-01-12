package dev.felix2000jp.springapplicationtemplate.notes.api;

import dev.felix2000jp.springapplicationtemplate.notes.application.NoteService;
import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.CreateNoteDto;
import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.NoteDto;
import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.NoteListDto;
import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.UpdateNoteDto;
import dev.felix2000jp.springapplicationtemplate.notes.domain.exceptions.NoteNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = NoteController.class)
class NoteControllerTest {

    @MockitoBean
    private NoteService noteService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser
    void should_get_page_of_notes_from_the_logged_in_user_when_page_query_param_is_valid() throws Exception {
        var noteDTO = new NoteDto(UUID.randomUUID(), "title", "content");
        var noteListDTO = new NoteListDto(List.of(noteDTO));

        var expectedResponse = String.format("""
                {
                    "notes": [{ "id": "%s", "title": "%s", "content": "%s" }]
                }
                """, noteDTO.id(), noteDTO.title(), noteDTO.content());

        when(noteService.getByCurrentUser(0)).thenReturn(noteListDTO);

        mockMvc
                .perform(get("/api/notes"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));

        mockMvc
                .perform(get("/api/notes?page=0"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @ParameterizedTest
    @ValueSource(strings = {"-1", "not a number"})
    @WithMockUser
    void should_fail_with_400_when_getting_page_of_notes_from_the_logged_in_user_with_invalid_page(String page) throws Exception {
        mockMvc
                .perform(get("/api/notes?page=" + page))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @WithMockUser
    void should_get_note_with_id_when_note_exists() throws Exception {
        var noteDTO = new NoteDto(UUID.randomUUID(), "title", "content");
        var expectedResponse = String.format("""
                {
                    "id": "%s",
                    "title": "%s",
                    "content": "%s"
                }
                """, noteDTO.id(), noteDTO.title(), noteDTO.content());

        when(noteService.getByIdAndCurrentUser(noteDTO.id())).thenReturn(noteDTO);

        mockMvc
                .perform(get("/api/notes/" + noteDTO.id()))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    @WithMockUser
    void should_fail_with_404_when_getting_note_with_id_that_does_not_exist() throws Exception {
        var exception = new NoteNotFoundException();
        when(noteService.getByIdAndCurrentUser(any())).thenThrow(exception);

        mockMvc
                .perform(get("/api/notes/" + UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.detail").value(exception.getMessage()))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @WithMockUser
    void should_respond_with_201_when_note_is_created_successfully() throws Exception {
        var noteDTO = new NoteDto(UUID.randomUUID(), "title", "content");
        var createNoteDTO = new CreateNoteDto(noteDTO.title(), noteDTO.content());

        var requestBody = String.format("""
                { "title": "%s", "content": "%s" }
                """, createNoteDTO.title(), createNoteDTO.content());
        var expectedResponse = String.format("""
                { "id": "%s", "title": "%s", "content": "%s" }
                """, noteDTO.id(), noteDTO.title(), noteDTO.content());

        when(noteService.createByCurrentUser(createNoteDTO)).thenReturn(noteDTO);

        mockMvc
                .perform(post("/api/notes").with(csrf()).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(content().json(expectedResponse));
    }

    @ParameterizedTest
    @MethodSource
    @WithMockUser
    void should_fail_with_400_when_create_request_body_is_invalid(String requestBody) throws Exception {
        mockMvc
                .perform(post("/api/notes").with(csrf()).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @WithMockUser
    void should_respond_with_204_when_note_is_updated_successfully() throws Exception {
        var noteDTO = new NoteDto(UUID.randomUUID(), "title", "content");
        var updateNoteDTO = new UpdateNoteDto(noteDTO.title(), noteDTO.content());

        var requestBody = String.format("""
                { "title": "%s", "content": "%s" }
                """, updateNoteDTO.title(), updateNoteDTO.content());

        when(noteService.updateByIdAndCurrentUser(noteDTO.id(), updateNoteDTO)).thenReturn(noteDTO);

        mockMvc
                .perform(put("/api/notes/" + noteDTO.id()).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isNoContent());
    }

    @ParameterizedTest
    @MethodSource
    @WithMockUser
    void should_fail_with_400_when_update_request_body_is_invalid(String requestBody) throws Exception {
        mockMvc
                .perform(put("/api/notes/" + UUID.randomUUID()).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @WithMockUser
    void should_respond_with_204_when_note_is_deleted_successfully() throws Exception {
        var noteDTO = new NoteDto(UUID.randomUUID(), "title", "content");

        when(noteService.deleteByIdAndCurrentUser(noteDTO.id())).thenReturn(noteDTO);

        mockMvc
                .perform(delete("/api/notes/" + noteDTO.id()).with(csrf()))
                .andExpect(status().isNoContent());

    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "not a uuid"})
    @WithMockUser
    void should_fail_with_400_when_deleting_note_given_an_invalid_note_id(String noteId) throws Exception {
        mockMvc
                .perform(delete("/api/notes/" + noteId).with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400));
    }

    private static Stream<Arguments> should_fail_with_400_when_create_request_body_is_invalid() {
        return Stream.of(
                arguments(""),
                arguments("{}"),
                arguments("{ 'title': 'title' }"),
                arguments("{ 'content': 'content' }"),
                arguments("{ 'title': null, 'content': 'content' }"),
                arguments("{ 'title': '', 'content': 'content' }"),
                arguments("{ 'title': ' ', 'content': 'content' }"),
                arguments("{ 'title': 'title', 'content': null }"),
                arguments("{ 'title': 'title', 'content': '' }"),
                arguments("{ 'title': 'title', 'content': ' ' }")
        );
    }

    private static Stream<Arguments> should_fail_with_400_when_update_request_body_is_invalid() {
        return Stream.of(
                arguments(""),
                arguments("{}"),
                arguments("{ 'title': 'title' }"),
                arguments("{ 'content': 'content' }"),
                arguments("{ 'title': null, 'content': 'content' }"),
                arguments("{ 'title': '', 'content': 'content' }"),
                arguments("{ 'title': ' ', 'content': 'content' }"),
                arguments("{ 'title': 'title', 'content': null }"),
                arguments("{ 'title': 'title', 'content': '' }"),
                arguments("{ 'title': 'title', 'content': ' ' }")
        );
    }

}
