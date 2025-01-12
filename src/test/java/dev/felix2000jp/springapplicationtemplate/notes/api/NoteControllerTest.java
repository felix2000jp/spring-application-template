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
    void givenPage_whenGetByPage_thenReturnOkAndNoteListDto() throws Exception {
        // given
        var page = 0;
        var noteDto = new NoteDto(UUID.randomUUID(), "title", "content");
        var noteListDto = new NoteListDto(List.of(noteDto));

        var expectedResponse = String.format("""
                {
                    "notes": [{ "id": "%s", "title": "%s", "content": "%s" }]
                }
                """, noteDto.id(), noteDto.title(), noteDto.content());

        when(noteService.getNotesForCurrentUser(page)).thenReturn(noteListDto);

        // when and then
        mockMvc
                .perform(get("/api/notes"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));

        mockMvc
                .perform(get("/api/notes?page=" + page))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @ParameterizedTest
    @ValueSource(strings = {"-1", "not a number"})
    @WithMockUser
    void givenInvalidPage_whenGetByPage_thenReturnBadRequest(String page) throws Exception {
        // when and then
        mockMvc
                .perform(get("/api/notes?page=" + page))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @WithMockUser
    void givenId_whenGetById_thenReturnOkAndNoteDto() throws Exception {
        // given
        var id = UUID.randomUUID();
        var noteDto = new NoteDto(id, "title", "content");
        var expectedResponse = String.format("""
                {
                    "id": "%s",
                    "title": "%s",
                    "content": "%s"
                }
                """, noteDto.id(), noteDto.title(), noteDto.content());

        when(noteService.getNoteByIdForCurrentUser(id)).thenReturn(noteDto);

        // when and then
        mockMvc
                .perform(get("/api/notes/" + id))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    @WithMockUser
    void givenNonExistentId_whenGetById_thenReturnNotFound() throws Exception {
        // given
        var exception = new NoteNotFoundException();
        when(noteService.getNoteByIdForCurrentUser(any())).thenThrow(exception);

        // when and then
        mockMvc
                .perform(get("/api/notes/" + UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.detail").value(exception.getMessage()))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @WithMockUser
    void givenCreateNoteDto_whenCreate_thenReturnCreatedAndNoteDto() throws Exception {
        // given
        var noteDto = new NoteDto(UUID.randomUUID(), "title", "content");
        var createNoteDTO = new CreateNoteDto(noteDto.title(), noteDto.content());

        var requestBody = String.format("""
                { "title": "%s", "content": "%s" }
                """, createNoteDTO.title(), createNoteDTO.content());
        var expectedResponse = String.format("""
                { "id": "%s", "title": "%s", "content": "%s" }
                """, noteDto.id(), noteDto.title(), noteDto.content());

        when(noteService.createNoteForCurrentUser(createNoteDTO)).thenReturn(noteDto);

        // when and then
        mockMvc
                .perform(post("/api/notes").with(csrf()).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(content().json(expectedResponse));
    }

    @ParameterizedTest
    @MethodSource
    @WithMockUser
    void givenInvalidCreateNoteDto_whenCreate_thenReturnBadRequest(String requestBody) throws Exception {
        // when and then
        mockMvc
                .perform(post("/api/notes").with(csrf()).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @WithMockUser
    void givenIdAndUpdateNoteDto_whenUpdate_thenReturnNoContent() throws Exception {
        // given
        var id = UUID.randomUUID();
        var noteDto = new NoteDto(id, "title", "content");
        var updateNoteDto = new UpdateNoteDto(noteDto.title(), noteDto.content());

        var requestBody = String.format("""
                { "title": "%s", "content": "%s" }
                """, updateNoteDto.title(), updateNoteDto.content());

        when(noteService.updateNoteByIdForCurrentUser(id, updateNoteDto)).thenReturn(noteDto);

        // when and then
        mockMvc
                .perform(put("/api/notes/" + id).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void givenNonExistentIdAndUpdateNoteDto_whenUpdate_thenReturnNotFound() throws Exception {
        // given
        var id = UUID.randomUUID();
        var updateNoteDto = new UpdateNoteDto("title", "content");

        var requestBody = String.format("""
                { "title": "%s", "content": "%s" }
                """, updateNoteDto.title(), updateNoteDto.content());

        var exception = new NoteNotFoundException();
        when(noteService.updateNoteByIdForCurrentUser(id, updateNoteDto)).thenThrow(exception);

        // when and then
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
    void givenInvalidUpdateNoteDto_whenUpdate_thenReturnBadRequest(String requestBody) throws Exception {
        // when and then
        mockMvc
                .perform(put("/api/notes/" + UUID.randomUUID()).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @WithMockUser
    void givenId_whenDelete_thenReturnNoContent() throws Exception {
        // given
        var id = UUID.randomUUID();
        var noteDto = new NoteDto(id, "title", "content");

        when(noteService.deleteNoteByIdForCurrentUser(id)).thenReturn(noteDto);

        // when and then
        mockMvc
                .perform(delete("/api/notes/" + id).with(csrf()))
                .andExpect(status().isNoContent());

    }

    @Test
    @WithMockUser
    void givenNonExistentId_whenDelete_thenReturnNotFound() throws Exception {
        // given
        var id = UUID.randomUUID();

        var exception = new NoteNotFoundException();
        when(noteService.deleteNoteByIdForCurrentUser(id)).thenThrow(exception);

        // when and then
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
    void givenInvalidId_whenDelete_thenReturnBadRequest(String id) throws Exception {
        // when and then
        mockMvc
                .perform(delete("/api/notes/" + id).with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400));
    }

    private static Stream<Arguments> givenInvalidCreateNoteDto_whenCreate_thenReturnBadRequest() {
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

    private static Stream<Arguments> givenInvalidUpdateNoteDto_whenUpdate_thenReturnBadRequest() {
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
