package dev.felix2000jp.springapplicationtemplate.notes.infrastructure.api;

import dev.felix2000jp.springapplicationtemplate.notes.application.NoteService;
import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.CreateNoteDto;
import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.NoteDto;
import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.NoteListDto;
import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.UpdateNoteDto;
import dev.felix2000jp.springapplicationtemplate.notes.domain.exceptions.NoteNotFoundException;
import io.micrometer.tracing.Tracer;
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
    private Tracer tracer;
    @MockitoBean
    private NoteService noteService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser
    void getNotesForCurrentUser_given_page_param_then_return_200_and_page_of_notes() throws Exception {
        var noteDto = new NoteDto(UUID.randomUUID(), "title", "content");
        var noteListDto = new NoteListDto(List.of(noteDto));

        var expectedResponse = String.format("""
                {
                    "notes": [{ "id": "%s", "title": "%s", "content": "%s" }]
                }
                """, noteDto.id(), noteDto.title(), noteDto.content());

        when(noteService.getNotesForCurrentUser(0)).thenReturn(noteListDto);

        // when and then
        mockMvc
                .perform(get("/api/notes"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));

        mockMvc
                .perform(get("/api/notes?page=" + 0))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @ParameterizedTest
    @ValueSource(strings = {"-1", "not a number"})
    @WithMockUser
    void getNotesForCurrentUser_given_invalid_page_param_then_return_400(String page) throws Exception {
        mockMvc
                .perform(get("/api/notes?page=" + page))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @WithMockUser
    void getNoteByIdForCurrentUser_given_id_then_return_200_and_note() throws Exception {
        var noteDto = new NoteDto(UUID.randomUUID(), "title", "content");
        var expectedResponse = String.format("""
                {
                    "id": "%s",
                    "title": "%s",
                    "content": "%s"
                }
                """, noteDto.id(), noteDto.title(), noteDto.content());

        when(noteService.getNoteByIdForCurrentUser(noteDto.id())).thenReturn(noteDto);

        mockMvc
                .perform(get("/api/notes/" + noteDto.id()))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    @WithMockUser
    void getNoteByIdForCurrentUser_given_not_found_id_then_return_404() throws Exception {
        var exception = new NoteNotFoundException();
        when(noteService.getNoteByIdForCurrentUser(any())).thenThrow(exception);

        mockMvc
                .perform(get("/api/notes/" + UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.detail").value(exception.getMessage()))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @WithMockUser
    void createNoteForCurrentUser_given_valid_body_then_return_201_and_location_header_and_note() throws Exception {
        var noteDto = new NoteDto(UUID.randomUUID(), "title", "content");
        var createNoteDto = new CreateNoteDto(noteDto.title(), noteDto.content());

        var requestBody = String.format("""
                { "title": "%s", "content": "%s" }
                """, createNoteDto.title(), createNoteDto.content());
        var expectedResponse = String.format("""
                { "id": "%s", "title": "%s", "content": "%s" }
                """, noteDto.id(), noteDto.title(), noteDto.content());

        when(noteService.createNoteForCurrentUser(createNoteDto)).thenReturn(noteDto);

        mockMvc
                .perform(post("/api/notes").with(csrf()).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(header().string("LOCATION", "/api/notes/" + noteDto.id()))
                .andExpect(content().json(expectedResponse));
    }

    @ParameterizedTest
    @MethodSource
    @WithMockUser
    void createNoteForCurrentUser_given_invalid_request_body_then_return_404(String requestBody) throws Exception {
        mockMvc
                .perform(post("/api/notes").with(csrf()).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @WithMockUser
    void updateNoteByIdForCurrentUser_given_id_param_and_body_then_return_204() throws Exception {
        var id = UUID.randomUUID();
        var noteDto = new NoteDto(id, "title", "content");
        var updateNoteDto = new UpdateNoteDto(noteDto.title(), noteDto.content());

        var requestBody = String.format("""
                { "title": "%s", "content": "%s" }
                """, updateNoteDto.title(), updateNoteDto.content());

        when(noteService.updateNoteByIdForCurrentUser(id, updateNoteDto)).thenReturn(noteDto);

        mockMvc
                .perform(put("/api/notes/" + id).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void updateNoteByIdForCurrentUser_given_not_found_id_and_body_then_return_404() throws Exception {
        var id = UUID.randomUUID();
        var updateNoteDto = new UpdateNoteDto("title", "content");

        var requestBody = String.format("""
                { "title": "%s", "content": "%s" }
                """, updateNoteDto.title(), updateNoteDto.content());

        var exception = new NoteNotFoundException();
        when(noteService.updateNoteByIdForCurrentUser(id, updateNoteDto)).thenThrow(exception);

        mockMvc
                .perform(put("/api/notes/" + id).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.detail").value(exception.getMessage()))
                .andExpect(jsonPath("$.status").value(404));
    }

    @ParameterizedTest
    @MethodSource
    @WithMockUser
    void updateNoteByIdForCurrentUse_given_invalid_request_body_then_return_400(String requestBody) throws Exception {
        mockMvc
                .perform(put("/api/notes/" + UUID.randomUUID()).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @WithMockUser
    void deleteNoteByIdForCurrentUser_given_id_then_return_204() throws Exception {
        var id = UUID.randomUUID();
        var noteDto = new NoteDto(id, "title", "content");

        when(noteService.deleteNoteByIdForCurrentUser(id)).thenReturn(noteDto);

        mockMvc
                .perform(delete("/api/notes/" + id).with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void deleteNoteByIdForCurrentUser_given_not_found_id_then_return_404() throws Exception {
        var id = UUID.randomUUID();
        var exception = new NoteNotFoundException();
        when(noteService.deleteNoteByIdForCurrentUser(id)).thenThrow(exception);

        mockMvc
                .perform(delete("/api/notes/" + id).with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.detail").value(exception.getMessage()))
                .andExpect(jsonPath("$.status").value(404));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "not a uuid"})
    @WithMockUser
    void deleteNoteByIdForCurrentUser_given_invalid_id_then_return_400(String id) throws Exception {
        mockMvc
                .perform(delete("/api/notes/" + id).with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400));
    }

    private static Stream<Arguments> createNoteForCurrentUser_given_invalid_request_body_then_return_404() {
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

    private static Stream<Arguments> updateNoteByIdForCurrentUse_given_invalid_request_body_then_return_400() {
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
