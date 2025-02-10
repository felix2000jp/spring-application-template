package dev.felix2000jp.springapplicationtemplate.notes.infrastructure.api;

import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.CreateNoteDto;
import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.NoteDto;
import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.NoteListDto;
import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.UpdateNoteDto;
import dev.felix2000jp.springapplicationtemplate.notes.domain.Note;
import dev.felix2000jp.springapplicationtemplate.notes.domain.NoteRepository;
import dev.felix2000jp.springapplicationtemplate.auth.domain.SecurityScope;
import dev.felix2000jp.springapplicationtemplate.auth.application.SecurityService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ApplicationModuleTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class NoteControllerIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres");

    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private NoteRepository noteRepository;
    @Autowired
    private SecurityService securityService;

    private Note note;
    private HttpHeaders headers;

    @BeforeEach
    void setUp() {
        var appuserId = UUID.randomUUID();
        var token = securityService.generateToken(
                "username",
                appuserId.toString(),
                SecurityScope.APPLICATION.name()
        );

        headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);

        note = new Note(appuserId, "title", "content");
        noteRepository.save(note);
    }

    @AfterEach
    void tearDown() {
        noteRepository.deleteAllByAppuserId(note.getAppuserId());
    }

    @Test
    void getNotesForCurrentUser_given_user_with_notes_then_return_user_notes() {
        var findNoteBydIdEntity = testRestTemplate.exchange(
                "/api/notes?page={page}",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                NoteListDto.class,
                0
        );

        assertThat(findNoteBydIdEntity.getStatusCode().value()).isEqualTo(200);
        assertThat(findNoteBydIdEntity.getBody()).isNotNull();
        assertThat(findNoteBydIdEntity.getBody().notes()).hasSize(1);
    }

    @Test
    void getNoteByIdForCurrentUser_given_user_with_note_then_return_note() {
        var findNoteBydIdEntity = testRestTemplate.exchange(
                "/api/notes/{id}",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                NoteDto.class,
                note.getId()
        );

        assertThat(findNoteBydIdEntity.getStatusCode().value()).isEqualTo(200);
        assertThat(findNoteBydIdEntity.getBody()).isNotNull();
        assertThat(findNoteBydIdEntity.getBody().title()).isEqualTo(note.getTitle());
        assertThat(findNoteBydIdEntity.getBody().content()).isEqualTo(note.getContent());
    }

    @Test
    void createNoteForCurrentUser_given_user_then_create_note() {
        var createNoteEntity = testRestTemplate.exchange(
                "/api/notes",
                HttpMethod.POST,
                new HttpEntity<>(new CreateNoteDto("title", "content"), headers),
                NoteDto.class
        );

        assertThat(createNoteEntity.getStatusCode().value()).isEqualTo(201);
        assertThat(createNoteEntity.getBody()).isNotNull();
        assertThat(createNoteEntity.getBody().title()).isEqualTo("title");
        assertThat(createNoteEntity.getBody().content()).isEqualTo("content");

        var createdNote = noteRepository.findByIdAndAppuserId(note.getId(), note.getAppuserId());
        assertThat(createdNote).isPresent();
        assertThat(createdNote.get().getTitle()).isEqualTo("title");
        assertThat(createdNote.get().getContent()).isEqualTo("content");
    }

    @Test
    void updateNoteByIdForCurrentUser_given_user_with_note_then_update_note() {
        var updateNoteEntity = testRestTemplate.exchange(
                "/api/notes/{id}",
                HttpMethod.PUT,
                new HttpEntity<>(new UpdateNoteDto("new title", "new content"), headers),
                Void.class,
                note.getId()
        );

        assertThat(updateNoteEntity.getStatusCode().value()).isEqualTo(204);

        var updatedNote = noteRepository.findByIdAndAppuserId(note.getId(), note.getAppuserId());
        assertThat(updatedNote).isPresent();
        assertThat(updatedNote.get().getTitle()).isEqualTo("new title");
        assertThat(updatedNote.get().getContent()).isEqualTo("new content");
    }

    @Test
    void deleteNoteByIdForCurrentUser_given_user_with_note_then_delete_note() {
        var deleteNoteEntity = testRestTemplate.exchange(
                "/api/notes/{id}",
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                Void.class,
                note.getId()
        );

        assertThat(deleteNoteEntity.getStatusCode().value()).isEqualTo(204);

        var deletedNote = noteRepository.findByIdAndAppuserId(note.getId(), note.getAppuserId());
        assertThat(deletedNote).isNotPresent();
    }

}
