package dev.felix2000jp.springapplicationtemplate.notes.api;

import dev.felix2000jp.springapplicationtemplate.shared.SecurityService;
import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.CreateNoteDto;
import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.NoteDto;
import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.NoteListDto;
import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.UpdateNoteDto;
import dev.felix2000jp.springapplicationtemplate.notes.domain.NoteRepository;
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

import static org.junit.jupiter.api.Assertions.*;

@ApplicationModuleTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class NoteControllerIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres");

    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private SecurityService securityService;
    @Autowired
    private NoteRepository noteRepository;

    private UUID authenticatedUserId;
    private HttpHeaders headersWithJwtToken;

    @BeforeEach
    void setUp() {
        authenticatedUserId = UUID.randomUUID();
        headersWithJwtToken = new HttpHeaders();

        var token = securityService.generateToken(
                "username",
                authenticatedUserId.toString(),
                SecurityService.Scope.APPLICATION.name()
        );
        headersWithJwtToken.add("Authorization", "Bearer " + token);
    }

    @Test
    void givenNotesInDatabase_whenUserFetchesNotes_thenReturnNoteListDto() {
        // given
        var createNoteEntity = testRestTemplate.exchange(
                "/api/notes",
                HttpMethod.POST,
                new HttpEntity<>(new CreateNoteDto("title", "content"), headersWithJwtToken),
                NoteDto.class
        );
        assertEquals(201, createNoteEntity.getStatusCode().value());
        assertNotNull(createNoteEntity.getBody());
        assertEquals("title", createNoteEntity.getBody().title());
        assertEquals("content", createNoteEntity.getBody().content());

        var noteInDatabase = noteRepository.findByIdAndAppuserId(createNoteEntity.getBody().id(), authenticatedUserId);
        assertNotNull(noteInDatabase);

        // when
        var findNoteBydIdEntity = testRestTemplate.exchange(
                "/api/notes?page={page}",
                HttpMethod.GET,
                new HttpEntity<>(headersWithJwtToken),
                NoteListDto.class,
                0
        );

        // then
        assertEquals(200, findNoteBydIdEntity.getStatusCode().value());
        assertNotNull(findNoteBydIdEntity.getBody());
        assertEquals(1, findNoteBydIdEntity.getBody().notes().size());
        assertEquals("title", findNoteBydIdEntity.getBody().notes().getFirst().title());
        assertEquals("content", findNoteBydIdEntity.getBody().notes().getFirst().content());
    }

    @Test
    void givenNoteInDatabase_whenUserFetchesNote_thenReturnNoteDto() {
        // given
        var createNoteEntity = testRestTemplate.exchange(
                "/api/notes",
                HttpMethod.POST,
                new HttpEntity<>(new CreateNoteDto("title", "content"), headersWithJwtToken),
                NoteDto.class
        );
        assertEquals(201, createNoteEntity.getStatusCode().value());
        assertNotNull(createNoteEntity.getBody());
        assertEquals("title", createNoteEntity.getBody().title());
        assertEquals("content", createNoteEntity.getBody().content());

        var noteInDatabase = noteRepository.findByIdAndAppuserId(createNoteEntity.getBody().id(), authenticatedUserId);
        assertNotNull(noteInDatabase);

        // when
        var findNoteBydIdEntity = testRestTemplate.exchange(
                "/api/notes/{id}",
                HttpMethod.GET,
                new HttpEntity<>(headersWithJwtToken),
                NoteDto.class,
                noteInDatabase.getId()
        );

        //then
        assertEquals(200, findNoteBydIdEntity.getStatusCode().value());
        assertNotNull(findNoteBydIdEntity.getBody());
        assertEquals("title", findNoteBydIdEntity.getBody().title());
        assertEquals("content", findNoteBydIdEntity.getBody().content());
    }

    @Test
    void givenNoteInDatabase_whenUserUpdatesNote_thenUpdateNote() {
        // give
        var createNoteEntity = testRestTemplate.exchange(
                "/api/notes",
                HttpMethod.POST,
                new HttpEntity<>(new CreateNoteDto("title", "content"), headersWithJwtToken),
                NoteDto.class
        );
        assertEquals(201, createNoteEntity.getStatusCode().value());
        assertNotNull(createNoteEntity.getBody());
        assertEquals("title", createNoteEntity.getBody().title());
        assertEquals("content", createNoteEntity.getBody().content());

        var noteInDatabase = noteRepository.findByIdAndAppuserId(createNoteEntity.getBody().id(), authenticatedUserId);
        assertNotNull(noteInDatabase);

        // when
        var updateNoteEntity = testRestTemplate.exchange(
                "/api/notes/{id}",
                HttpMethod.PUT,
                new HttpEntity<>(new UpdateNoteDto("new title", "new content"), headersWithJwtToken),
                Void.class,
                createNoteEntity.getBody().id()
        );
        assertEquals(204, updateNoteEntity.getStatusCode().value());

        // then
        var updatedNoteInDatabase = noteRepository.findByIdAndAppuserId(
                createNoteEntity.getBody().id(),
                authenticatedUserId
        );
        assertNotNull(updatedNoteInDatabase);
        assertEquals("new title", updatedNoteInDatabase.getTitle());
        assertEquals("new content", updatedNoteInDatabase.getContent());
    }

    @Test
    void givenNoteInDatabase_whenUserDeletesNote_thenDeleteNote() {
        // given
        var createNoteEntity = testRestTemplate.exchange(
                "/api/notes",
                HttpMethod.POST,
                new HttpEntity<>(new CreateNoteDto("title", "content"), headersWithJwtToken),
                NoteDto.class
        );
        assertEquals(201, createNoteEntity.getStatusCode().value());
        assertNotNull(createNoteEntity.getBody());
        assertEquals("title", createNoteEntity.getBody().title());
        assertEquals("content", createNoteEntity.getBody().content());

        var noteInDatabase = noteRepository.findByIdAndAppuserId(createNoteEntity.getBody().id(), authenticatedUserId);
        assertNotNull(noteInDatabase);

        // when
        var deleteNoteEntity = testRestTemplate.exchange(
                "/api/notes/{id}",
                HttpMethod.DELETE,
                new HttpEntity<>(headersWithJwtToken),
                Void.class,
                createNoteEntity.getBody().id()
        );
        assertEquals(204, deleteNoteEntity.getStatusCode().value());

        // then
        var deletedNoteInDatabase = noteRepository.findByIdAndAppuserId(
                createNoteEntity.getBody().id(),
                authenticatedUserId
        );
        assertNull(deletedNoteInDatabase);
    }

}
