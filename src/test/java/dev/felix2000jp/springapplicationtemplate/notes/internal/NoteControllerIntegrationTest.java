package dev.felix2000jp.springapplicationtemplate.notes.internal;

import dev.felix2000jp.springapplicationtemplate.TestcontainersConfiguration;
import dev.felix2000jp.springapplicationtemplate.appusers.internal.dtos.AppuserDTO;
import dev.felix2000jp.springapplicationtemplate.appusers.internal.dtos.CreateAppuserDTO;
import dev.felix2000jp.springapplicationtemplate.notes.internal.dtos.CreateNoteDTO;
import dev.felix2000jp.springapplicationtemplate.notes.internal.dtos.NoteDTO;
import dev.felix2000jp.springapplicationtemplate.notes.internal.dtos.NoteListDTO;
import dev.felix2000jp.springapplicationtemplate.notes.internal.dtos.UpdateNoteDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.security.web.csrf.DefaultCsrfToken;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@ApplicationModuleTest(extraIncludes = {"shared", "appusers"}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class NoteControllerIntegrationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    private HttpHeaders authenticatedHeaders;

    @BeforeEach
    void setUp() {
        authenticatedHeaders = new HttpHeaders();

        var csrfEntity = testRestTemplate.exchange("/api/appusers/csrf", HttpMethod.GET, null, DefaultCsrfToken.class);
        var csrfEntityStatusCode = csrfEntity.getStatusCode().value();
        var csrfEntityBody = csrfEntity.getBody();
        var csrfEntityHeaders = csrfEntity.getHeaders();

        assertThat(csrfEntityStatusCode).isEqualTo(200);
        assertThat(csrfEntityBody).isNotNull();
        assertThat(csrfEntityHeaders).isNotNull();

        authenticatedHeaders.addAll("Cookie", csrfEntityHeaders.getOrEmpty("SET-COOKIE"));
        authenticatedHeaders.add("X-Csrf-Token", csrfEntityBody.getToken());

        var createEntity = testRestTemplate.exchange(
                "/api/appusers",
                HttpMethod.POST,
                new HttpEntity<>(new CreateAppuserDTO("username", "password"), authenticatedHeaders),
                AppuserDTO.class
        );
        var createEntityStatusCode = createEntity.getStatusCode().value();
        var createEntityBody = createEntity.getBody();

        assertThat(createEntityStatusCode).isEqualTo(201);
        assertThat(createEntityBody).isNotNull();

        var tokenEntity = testRestTemplate
                .withBasicAuth("username", "password")
                .exchange("/api/appusers/token", HttpMethod.POST, new HttpEntity<>(authenticatedHeaders), String.class);
        var tokenEntityStatusCode = tokenEntity.getStatusCode().value();
        var tokenEntityBody = tokenEntity.getBody();

        assertThat(tokenEntityStatusCode).isEqualTo(200);
        assertThat(tokenEntityBody).isNotNull();

        authenticatedHeaders.add("Authorization", "Bearer " + tokenEntityBody);
    }

    @AfterEach
    void tearDown() {
        var deleteEntity = testRestTemplate.exchange(
                "/api/appusers",
                HttpMethod.DELETE,
                new HttpEntity<>(authenticatedHeaders),
                Void.class
        );
        var deleteEntityStatusCode = deleteEntity.getStatusCode().value();

        assertThat(deleteEntityStatusCode).isEqualTo(204);
    }

    @Test
    void should_not_create_notes_when_request_is_not_authenticated() {
        var createEntity = testRestTemplate.exchange(
                "/api/notes",
                HttpMethod.POST,
                new HttpEntity<>(new CreateNoteDTO("title", "content")),
                NoteDTO.class
        );
        var createEntityStatusCode = createEntity.getStatusCode().value();
        var createEntityBody = createEntity.getBody();

        assertThat(createEntityStatusCode).isEqualTo(401);
        assertThat(createEntityBody).isNull();
    }

    @Test
    void should_create_notes_and_find_all_of_them_when_request_is_authenticated() {
        createAndAssertNotes(3);

        var findAllEntity = testRestTemplate.exchange(
                "/api/notes",
                HttpMethod.GET,
                new HttpEntity<>(authenticatedHeaders),
                NoteListDTO.class
        );
        var findAllEntityStatusCode = findAllEntity.getStatusCode().value();
        var findAllEntityBody = findAllEntity.getBody();

        assertThat(findAllEntityStatusCode).isEqualTo(200);
        assertThat(findAllEntityBody).isNotNull();
        assertThat(findAllEntityBody.notes()).hasSize(3);
        assertThat(findAllEntityBody.notes()).allSatisfy(noteDto -> {
            assertThat(noteDto).isNotNull();
            assertThat(noteDto.title()).isEqualTo("title");
            assertThat(noteDto.content()).isEqualTo("content");
        });
    }

    @Test
    void should_create_notes_and_fail_to_find_them_when_request_is_not_authenticated() {
        createAndAssertNotes(3);

        var findAllEntity = testRestTemplate.exchange(
                "/api/notes",
                HttpMethod.GET,
                null,
                NoteListDTO.class
        );
        var findAllEntityStatusCode = findAllEntity.getStatusCode().value();
        var findAllEntityBody = findAllEntity.getBody();

        assertThat(findAllEntityStatusCode).isEqualTo(401);
        assertThat(findAllEntityBody).isNull();
    }

    @Test
    void should_create_note_and_update_it_when_request_is_authenticated() {
        var createdNoteDTO = createAndAssertNotes(1).getFirst();

        var updateEntity = testRestTemplate.exchange(
                "/api/notes/{id}",
                HttpMethod.PUT,
                new HttpEntity<>(new UpdateNoteDTO("new title", "new content"), authenticatedHeaders),
                Void.class,
                createdNoteDTO.id()
        );
        var updateEntityStatusCode = updateEntity.getStatusCode().value();

        assertThat(updateEntityStatusCode).isEqualTo(204);

        findAndAssertNoteById(true, createdNoteDTO.id(), "new title", "new content");
    }

    @Test
    void should_create_note_and_fail_to_update_it_when_request_is_not_authenticated() {
        var createdNoteDTO = createAndAssertNotes(1).getFirst();

        var updateEntity = testRestTemplate.exchange(
                "/api/notes/{id}",
                HttpMethod.PUT,
                new HttpEntity<>(new UpdateNoteDTO("new title", "new content")),
                Void.class,
                createdNoteDTO.id()
        );
        var updateEntityStatusCode = updateEntity.getStatusCode().value();

        assertThat(updateEntityStatusCode).isEqualTo(401);
    }

    @Test
    void should_create_note_and_delete_it_when_request_is_authenticated() {
        var createdNoteDTO = createAndAssertNotes(1).getFirst();

        var deleteEntity = testRestTemplate.exchange(
                "/api/notes/{id}",
                HttpMethod.DELETE,
                new HttpEntity<>(authenticatedHeaders),
                Void.class,
                createdNoteDTO.id()
        );
        var deleteEntityStatusCode = deleteEntity.getStatusCode().value();

        assertThat(deleteEntityStatusCode).isEqualTo(204);

        findAndAssertNoteById(false, createdNoteDTO.id(), null, null);
    }

    @Test
    void should_create_note_and_fail_to_delete_it_when_request_is_not_authenticated() {
        var createdNoteDTO = createAndAssertNotes(1).getFirst();

        var deleteEntity = testRestTemplate.exchange(
                "/api/notes/{id}",
                HttpMethod.DELETE,
                null,
                Void.class,
                createdNoteDTO.id()
        );
        var deleteEntityStatusCode = deleteEntity.getStatusCode().value();

        assertThat(deleteEntityStatusCode).isEqualTo(401);
    }

    private List<NoteDTO> createAndAssertNotes(int numberOfNotesToCreate) {
        var createdNotes = new ArrayList<NoteDTO>();

        for (var i = 0; i < numberOfNotesToCreate; i++) {
            var createEntity = testRestTemplate.exchange(
                    "/api/notes",
                    HttpMethod.POST,
                    new HttpEntity<>(new CreateNoteDTO("title", "content"), authenticatedHeaders),
                    NoteDTO.class
            );
            var createEntityStatusCode = createEntity.getStatusCode().value();
            var createEntityBody = createEntity.getBody();

            assertThat(createEntityStatusCode).isEqualTo(201);
            assertThat(createEntityBody).isNotNull();

            createdNotes.add(createEntityBody);
        }

        return createdNotes;
    }

    private void findAndAssertNoteById(boolean shouldFindUser, UUID id, String title, String content) {
        var findByIdEntity = testRestTemplate.exchange(
                "/api/notes/{id}",
                HttpMethod.GET,
                new HttpEntity<>(authenticatedHeaders),
                NoteDTO.class,
                id
        );
        var findByIdEntityStatusCode = findByIdEntity.getStatusCode().value();
        var findByIdEntityBody = findByIdEntity.getBody();

        if (shouldFindUser) {
            assertThat(findByIdEntityStatusCode).isEqualTo(200);
            assertThat(findByIdEntityBody).isNotNull();
            assertThat(findByIdEntityBody.title()).isEqualTo(title);
            assertThat(findByIdEntityBody.content()).isEqualTo(content);
        } else {
            assertThat(findByIdEntityStatusCode).isEqualTo(404);
        }
    }

}
