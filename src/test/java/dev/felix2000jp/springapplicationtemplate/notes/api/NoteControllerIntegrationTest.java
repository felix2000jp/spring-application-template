package dev.felix2000jp.springapplicationtemplate.notes.api;

import dev.felix2000jp.springapplicationtemplate.shared.SecurityClient;
import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.CreateNoteDTO;
import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.NoteDTO;
import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.NoteListDTO;
import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.UpdateNoteDTO;
import dev.felix2000jp.springapplicationtemplate.notes.domain.NoteRepository;
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
    private SecurityClient securityClient;
    @Autowired
    private NoteRepository noteRepository;

    @Test
    void should_create_and_get_note_by_page_successfully() {
        var authenticatedUserId = UUID.randomUUID();
        var token = securityClient.generateToken(
                "username",
                authenticatedUserId.toString(),
                SecurityClient.Scope.APPLICATION.name()
        );

        var headersWithJwtToken = new HttpHeaders();
        headersWithJwtToken.add("Authorization", "Bearer " + token);

        var createNoteEntity = testRestTemplate.exchange(
                "/api/notes",
                HttpMethod.POST,
                new HttpEntity<>(new CreateNoteDTO("title", "content"), headersWithJwtToken),
                NoteDTO.class
        );
        assertEquals(201, createNoteEntity.getStatusCode().value());
        assertNotNull(createNoteEntity.getBody());
        assertEquals("title", createNoteEntity.getBody().title());
        assertEquals("content", createNoteEntity.getBody().content());

        var noteInDatabase = noteRepository.getByIdAndAppuserId(createNoteEntity.getBody().id(), authenticatedUserId);
        assertNotNull(noteInDatabase);

        var findNoteBydIdEntity = testRestTemplate.exchange(
                "/api/notes?page={page}",
                HttpMethod.GET,
                new HttpEntity<>(headersWithJwtToken),
                NoteListDTO.class,
                0
        );
        assertEquals(200, findNoteBydIdEntity.getStatusCode().value());
        assertNotNull(findNoteBydIdEntity.getBody());
        assertEquals(1, findNoteBydIdEntity.getBody().notes().size());
        assertEquals("title", findNoteBydIdEntity.getBody().notes().getFirst().title());
        assertEquals("content", findNoteBydIdEntity.getBody().notes().getFirst().content());
    }

    @Test
    void should_create_and_get_note_by_id_successfully() {
        var authenticatedUserId = UUID.randomUUID();
        var token = securityClient.generateToken(
                "username",
                authenticatedUserId.toString(),
                SecurityClient.Scope.APPLICATION.name()
        );

        var headersWithJwtToken = new HttpHeaders();
        headersWithJwtToken.add("Authorization", "Bearer " + token);

        var createNoteEntity = testRestTemplate.exchange(
                "/api/notes",
                HttpMethod.POST,
                new HttpEntity<>(new CreateNoteDTO("title", "content"), headersWithJwtToken),
                NoteDTO.class
        );
        assertEquals(201, createNoteEntity.getStatusCode().value());
        assertNotNull(createNoteEntity.getBody());
        assertEquals("title", createNoteEntity.getBody().title());
        assertEquals("content", createNoteEntity.getBody().content());

        var noteInDatabase = noteRepository.getByIdAndAppuserId(createNoteEntity.getBody().id(), authenticatedUserId);
        assertNotNull(noteInDatabase);

        var findNoteBydIdEntity = testRestTemplate.exchange(
                "/api/notes/{id}",
                HttpMethod.GET,
                new HttpEntity<>(headersWithJwtToken),
                NoteDTO.class,
                noteInDatabase.getId()
        );
        assertEquals(200, findNoteBydIdEntity.getStatusCode().value());
        assertNotNull(findNoteBydIdEntity.getBody());
        assertEquals("title", findNoteBydIdEntity.getBody().title());
        assertEquals("content", findNoteBydIdEntity.getBody().content());
    }

    @Test
    void should_create_and_update_note_successfully() {
        var authenticatedUserId = UUID.randomUUID();
        var token = securityClient.generateToken(
                "username",
                authenticatedUserId.toString(),
                SecurityClient.Scope.APPLICATION.name()
        );

        var headersWithJwtToken = new HttpHeaders();
        headersWithJwtToken.add("Authorization", "Bearer " + token);

        var createNoteEntity = testRestTemplate.exchange(
                "/api/notes",
                HttpMethod.POST,
                new HttpEntity<>(new CreateNoteDTO("title", "content"), headersWithJwtToken),
                NoteDTO.class
        );
        assertEquals(201, createNoteEntity.getStatusCode().value());
        assertNotNull(createNoteEntity.getBody());
        assertEquals("title", createNoteEntity.getBody().title());
        assertEquals("content", createNoteEntity.getBody().content());

        var noteInDatabase = noteRepository.getByIdAndAppuserId(createNoteEntity.getBody().id(), authenticatedUserId);
        assertNotNull(noteInDatabase);

        var updateNoteEntity = testRestTemplate.exchange(
                "/api/notes/{id}",
                HttpMethod.PUT,
                new HttpEntity<>(new UpdateNoteDTO("new title", "new content"), headersWithJwtToken),
                Void.class,
                createNoteEntity.getBody().id()
        );
        assertEquals(204, updateNoteEntity.getStatusCode().value());

        var updatedNoteInDatabase = noteRepository.getByIdAndAppuserId(
                createNoteEntity.getBody().id(),
                authenticatedUserId
        );
        assertNotNull(updatedNoteInDatabase);
        assertEquals("new title", updatedNoteInDatabase.getTitle());
        assertEquals("new content", updatedNoteInDatabase.getContent());
    }

    @Test
    void should_create_and_delete_note_successfully() {
        var authenticatedUserId = UUID.randomUUID();
        var token = securityClient.generateToken(
                "username",
                authenticatedUserId.toString(),
                SecurityClient.Scope.APPLICATION.name()
        );

        var headersWithJwtToken = new HttpHeaders();
        headersWithJwtToken.add("Authorization", "Bearer " + token);

        var createNoteEntity = testRestTemplate.exchange(
                "/api/notes",
                HttpMethod.POST,
                new HttpEntity<>(new CreateNoteDTO("title", "content"), headersWithJwtToken),
                NoteDTO.class
        );
        assertEquals(201, createNoteEntity.getStatusCode().value());
        assertNotNull(createNoteEntity.getBody());
        assertEquals("title", createNoteEntity.getBody().title());
        assertEquals("content", createNoteEntity.getBody().content());

        var noteInDatabase = noteRepository.getByIdAndAppuserId(createNoteEntity.getBody().id(), authenticatedUserId);
        assertNotNull(noteInDatabase);

        var deleteNoteEntity = testRestTemplate.exchange(
                "/api/notes/{id}",
                HttpMethod.DELETE,
                new HttpEntity<>(headersWithJwtToken),
                Void.class,
                createNoteEntity.getBody().id()
        );
        assertEquals(204, deleteNoteEntity.getStatusCode().value());

        var deletedNoteInDatabase = noteRepository.getByIdAndAppuserId(
                createNoteEntity.getBody().id(),
                authenticatedUserId
        );
        assertNull(deletedNoteInDatabase);
    }

}
