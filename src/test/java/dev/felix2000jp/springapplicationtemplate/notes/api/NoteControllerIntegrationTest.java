package dev.felix2000jp.springapplicationtemplate.notes.api;

import dev.felix2000jp.springapplicationtemplate.shared.SecurityService;
import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.CreateNoteDto;
import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.NoteDto;
import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.NoteListDto;
import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.UpdateNoteDto;
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
    private SecurityService securityService;
    @Autowired
    private NoteRepository noteRepository;

    @Test
    void givenValidAuthenticationToken_whenNoteIsCreated_thenFetchPage() {
        // given
        var authenticatedUserId = UUID.randomUUID();
        var token = securityService.generateToken(
                "username",
                authenticatedUserId.toString(),
                SecurityService.Scope.APPLICATION.name()
        );

        var headersWithJwtToken = new HttpHeaders();
        headersWithJwtToken.add("Authorization", "Bearer " + token);

        // when
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

        var noteInDatabase = noteRepository.getByIdAndAppuserId(createNoteEntity.getBody().id(), authenticatedUserId);
        assertNotNull(noteInDatabase);

        // then
        var findNoteBydIdEntity = testRestTemplate.exchange(
                "/api/notes?page={page}",
                HttpMethod.GET,
                new HttpEntity<>(headersWithJwtToken),
                NoteListDto.class,
                0
        );
        assertEquals(200, findNoteBydIdEntity.getStatusCode().value());
        assertNotNull(findNoteBydIdEntity.getBody());
        assertEquals(1, findNoteBydIdEntity.getBody().notes().size());
        assertEquals("title", findNoteBydIdEntity.getBody().notes().getFirst().title());
        assertEquals("content", findNoteBydIdEntity.getBody().notes().getFirst().content());
    }

    @Test
    void givenValidAuthenticationToken_whenNoteIsCreated_thenFetchNote() {
        // given
        var authenticatedUserId = UUID.randomUUID();
        var token = securityService.generateToken(
                "username",
                authenticatedUserId.toString(),
                SecurityService.Scope.APPLICATION.name()
        );

        var headersWithJwtToken = new HttpHeaders();
        headersWithJwtToken.add("Authorization", "Bearer " + token);

        // when
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

        var noteInDatabase = noteRepository.getByIdAndAppuserId(createNoteEntity.getBody().id(), authenticatedUserId);
        assertNotNull(noteInDatabase);

        // then
        var findNoteBydIdEntity = testRestTemplate.exchange(
                "/api/notes/{id}",
                HttpMethod.GET,
                new HttpEntity<>(headersWithJwtToken),
                NoteDto.class,
                noteInDatabase.getId()
        );
        assertEquals(200, findNoteBydIdEntity.getStatusCode().value());
        assertNotNull(findNoteBydIdEntity.getBody());
        assertEquals("title", findNoteBydIdEntity.getBody().title());
        assertEquals("content", findNoteBydIdEntity.getBody().content());
    }

    @Test
    void givenValidAuthenticationToken_whenNoteIsCreated_thenUpdateNote() {
        // given
        var authenticatedUserId = UUID.randomUUID();
        var token = securityService.generateToken(
                "username",
                authenticatedUserId.toString(),
                SecurityService.Scope.APPLICATION.name()
        );

        var headersWithJwtToken = new HttpHeaders();
        headersWithJwtToken.add("Authorization", "Bearer " + token);

        // when and then
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

        var noteInDatabase = noteRepository.getByIdAndAppuserId(createNoteEntity.getBody().id(), authenticatedUserId);
        assertNotNull(noteInDatabase);

        var updateNoteEntity = testRestTemplate.exchange(
                "/api/notes/{id}",
                HttpMethod.PUT,
                new HttpEntity<>(new UpdateNoteDto("new title", "new content"), headersWithJwtToken),
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
    void givenValidAuthenticationToken_whenNoteIsCreated_thenDeleteNote() {
        // given
        var authenticatedUserId = UUID.randomUUID();
        var token = securityService.generateToken(
                "username",
                authenticatedUserId.toString(),
                SecurityService.Scope.APPLICATION.name()
        );

        var headersWithJwtToken = new HttpHeaders();
        headersWithJwtToken.add("Authorization", "Bearer " + token);

        // when and then
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
