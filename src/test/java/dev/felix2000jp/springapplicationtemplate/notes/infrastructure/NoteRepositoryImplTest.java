package dev.felix2000jp.springapplicationtemplate.notes.infrastructure;

import dev.felix2000jp.springapplicationtemplate.notes.domain.Note;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DataJpaTest
@Testcontainers
@Import(NoteRepositoryImpl.class)
class NoteRepositoryImplTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres");

    @Autowired
    private TestEntityManager testEntityManager;
    @Autowired
    private NoteRepositoryImpl noteRepository;

    @Test
    void should_find_notes_from_appuser_with_appuserId_when_page_is_not_empty() {
        var appuserId = UUID.randomUUID();

        testEntityManager.persistAndFlush(new Note(appuserId, "title 1", "content 1"));
        testEntityManager.persistAndFlush(new Note(appuserId, "title 2", "content 2"));

        var actual = noteRepository.getByAppuserId(appuserId, 0);

        assertFalse(actual.isEmpty());
    }

    @Test
    void should_not_find_notes_from_appuser_with_appuserId_when_page_is_empty() {
        var appuserId = UUID.randomUUID();

        testEntityManager.persistAndFlush(new Note(appuserId, "title 1", "content 1"));
        testEntityManager.persistAndFlush(new Note(appuserId, "title 2", "content 2"));

        var actual = noteRepository.getByAppuserId(appuserId, 1);

        assertTrue(actual.isEmpty());
    }

    @Test
    void should_find_note_with_id_from_appuser_with_appuserId_when_note_exists() {
        var note = new Note(UUID.randomUUID(), "title", "content");

        testEntityManager.persistAndFlush(note);

        var actual = noteRepository.getByIdAndAppuserId(note.getId(), note.getAppuserId());

        assertNotNull(actual);
    }

    @Test
    void should_not_find_note_with_id_from_appuser_with_appuserId_when_note_does_not_exist() {
        var note = new Note(UUID.randomUUID(), "title", "content");

        var actual = noteRepository.getByIdAndAppuserId(note.getId(), note.getAppuserId());

        assertNull(actual);
    }

    @Test
    void should_delete_note_with_id_when_note_exists() {
        var note = new Note(UUID.randomUUID(), "title", "content");

        testEntityManager.persistAndFlush(note);

        noteRepository.deleteById(note.getId());
        testEntityManager.flush();
        
        assertNull(testEntityManager.find(Note.class, note.getId()));
    }

    @Test
    void should_not_throw_when_trying_to_delete_note_with_id_when_note_does_not_exist() {
        assertDoesNotThrow(() -> {
            noteRepository.deleteById(UUID.randomUUID());
            testEntityManager.flush();
        });
    }

    @Test
    void should_delete_notes_from_appuser_with_id_when_notes_exist() {
        var appuserId = UUID.randomUUID();
        var note1 = new Note(appuserId, "title", "content");
        var note2 = new Note(appuserId, "title", "content");

        testEntityManager.persistAndFlush(note1);
        testEntityManager.persistAndFlush(note2);

        noteRepository.deleteByAppuserId(appuserId);
        testEntityManager.flush();

        assertNull(testEntityManager.find(Note.class, note1.getId()));
        assertNull(testEntityManager.find(Note.class, note2.getId()));
    }

    @Test
    void should_not_throw_when_trying_to_delete_notes_from_appuser_with_id_and_notes_do_not_exist() {
        assertDoesNotThrow(() -> {
            noteRepository.deleteByAppuserId(UUID.randomUUID());
            testEntityManager.flush();
        });
    }

    @Test
    void should_save_note_successfully() {
        var note = new Note(UUID.randomUUID(), "title 1", "content 1");

        noteRepository.save(note);
        testEntityManager.flush();

        assertNotNull(testEntityManager.find(Note.class, note.getId()));
    }

    @ParameterizedTest
    @MethodSource
    void should_fail_to_save_note_when_note_data_is_invalid(Note note) {
        assertThrows(Exception.class, () -> {
            noteRepository.save(note);
            testEntityManager.flush();
        });
    }

    private static Stream<Arguments> should_fail_to_save_note_when_note_data_is_invalid() {
        return Stream.of(
                arguments(new Note()),
                arguments(new Note(null, "title", "content")),
                arguments(new Note(UUID.randomUUID(), null, "content")),
                arguments(new Note(UUID.randomUUID(), "", "content")),
                arguments(new Note(UUID.randomUUID(), " ", "content")),
                arguments(new Note(UUID.randomUUID(), "title", null)),
                arguments(new Note(UUID.randomUUID(), "title", "")),
                arguments(new Note(UUID.randomUUID(), "title", " "))
        );
    }

}
