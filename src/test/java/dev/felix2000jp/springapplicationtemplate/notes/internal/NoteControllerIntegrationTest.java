package dev.felix2000jp.springapplicationtemplate.notes.internal;

import dev.felix2000jp.springapplicationtemplate.appusers.internal.AppuserRepository;
import dev.felix2000jp.springapplicationtemplate.appusers.internal.dtos.AppuserDto;
import dev.felix2000jp.springapplicationtemplate.appusers.internal.dtos.CreateAppuserDto;
import dev.felix2000jp.springapplicationtemplate.notes.internal.dtos.CreateNoteDto;
import dev.felix2000jp.springapplicationtemplate.notes.internal.dtos.NoteDto;
import dev.felix2000jp.springapplicationtemplate.notes.internal.dtos.NoteListDto;
import dev.felix2000jp.springapplicationtemplate.notes.internal.dtos.UpdateNoteDto;
import dev.felix2000jp.springapplicationtemplate.TestcontainersConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.modulith.test.ApplicationModuleTest;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@ApplicationModuleTest(extraIncludes = {"shared", "appusers"}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class NoteControllerIntegrationTest {

    @Autowired
    private AppuserRepository appuserRepository;
    @Autowired
    private NoteRepository noteRepository;
    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        restTemplate
                .postForEntity("/app/appusers", new CreateAppuserDto("username", "password"), AppuserDto.class)
                .getBody();

        var accessToken = restTemplate
                .withBasicAuth("username", "password")
                .postForEntity("/app/appusers/token", null, String.class)
                .getBody();

        restTemplate
                .getRestTemplate()
                .getInterceptors()
                .add((request, body, execution) -> {
                    request.getHeaders().add("Authorization", "Bearer " + accessToken);
                    return execution.execute(request, body);
                });
    }

    @AfterEach
    void tearDown() {
        appuserRepository.deleteAll();
        noteRepository.deleteAll();
        restTemplate.getRestTemplate().getInterceptors().clear();
    }

    @Test
    void should_create_notes_and_find_all_of_them() {
        var numberOfNote = 3;

        for (var i = 0; i < numberOfNote; i++) {
            var response = restTemplate.postForEntity(
                    "/api/notes",
                    new CreateNoteDto("title", "content"),
                    AppuserDto.class
            );

            assertThat(response.getStatusCode().value()).isEqualTo(201);
        }

        var response = restTemplate.getForEntity("/api/notes", NoteListDto.class);

        var statusCode = response.getStatusCode().value();
        var body = response.getBody();
        assertThat(statusCode).isEqualTo(200);
        assertThat(body).isNotNull();
        assertThat(body.notes()).hasSize(3);
        assertThat(body.notes()).allSatisfy(noteDto -> {
            assertThat(noteDto).isNotNull();
            assertThat(noteDto.title()).isEqualTo("title");
            assertThat(noteDto.content()).isEqualTo("content");
        });
    }

    @Test
    void should_create_note_and_find_it_by_id() {
        var createResponse = restTemplate.postForEntity(
                "/api/notes",
                new CreateNoteDto("title", "content"),
                AppuserDto.class
        );

        var createStatusCode = createResponse.getStatusCode().value();
        var createBody = createResponse.getBody();
        assertThat(createStatusCode).isEqualTo(201);
        assertThat(createBody).isNotNull();

        var getResponse = restTemplate.getForEntity("/api/notes/{id}", NoteDto.class, createBody.id());

        var getStatusCode = getResponse.getStatusCode().value();
        var getBody = getResponse.getBody();
        assertThat(getStatusCode).isEqualTo(200);
        assertThat(getBody).isNotNull();
        assertThat(getBody.title()).isEqualTo("title");
        assertThat(getBody.content()).isEqualTo("content");
    }

    @Test
    void should_create_note_and_update_its_contents() {
        var createResponse = restTemplate.postForEntity(
                "/api/notes",
                new CreateNoteDto("title", "content"),
                AppuserDto.class
        );

        var createStatusCode = createResponse.getStatusCode().value();
        var createBody = createResponse.getBody();
        assertThat(createStatusCode).isEqualTo(201);
        assertThat(createBody).isNotNull();

        restTemplate.put(
                "/api/notes/{id}",
                new UpdateNoteDto("new title", "new content"),
                createBody.id()
        );

        var getResponse = restTemplate.getForEntity("/api/notes/{id}", NoteDto.class, createBody.id());

        var getStatusCode = getResponse.getStatusCode().value();
        var getBody = getResponse.getBody();
        assertThat(getStatusCode).isEqualTo(200);
        assertThat(getBody).isNotNull();
        assertThat(getBody.title()).isEqualTo("new title");
        assertThat(getBody.content()).isEqualTo("new content");
    }

    @Test
    void should_create_note_and_delete_it() {
        var createResponse = restTemplate.postForEntity(
                "/api/notes",
                new CreateNoteDto("title", "content"),
                AppuserDto.class
        );

        var createStatusCode = createResponse.getStatusCode().value();
        var createBody = createResponse.getBody();
        assertThat(createStatusCode).isEqualTo(201);
        assertThat(createBody).isNotNull();

        restTemplate.delete("/api/notes/{id}", createBody.id());

        var getResponse = restTemplate.getForEntity("/api/notes/{id}", NoteDto.class, createBody.id());

        var getStatusCode = getResponse.getStatusCode().value();
        assertThat(getStatusCode).isEqualTo(404);
    }

}
