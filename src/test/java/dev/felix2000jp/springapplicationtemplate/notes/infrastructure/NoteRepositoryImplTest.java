package dev.felix2000jp.springapplicationtemplate.notes.infrastructure;

import dev.felix2000jp.springapplicationtemplate.notes.domain.Note;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
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
    private EntityManager entityManager;
    @Autowired
    private NoteRepositoryImpl noteRepository;

    @BeforeEach
    void setUp() {
        entityManager
                .createNativeQuery("TRUNCATE TABLE note")
                .executeUpdate();
    }

    @Test
    void should_find_notes_from_appuser_with_appuserId_when_page_is_not_empty() {
        var appuserId = UUID.randomUUID();

        entityManager.persist(new Note(appuserId, "title 1", "content 1"));
        entityManager.persist(new Note(appuserId, "title 2", "content 2"));

        entityManager.flush();
        entityManager.clear();

        var actual = noteRepository.getByAppuserId(appuserId, 0);

        assertFalse(actual.isEmpty());
    }

    @Test
    void should_not_find_notes_from_appuser_with_appuserId_when_page_is_empty() {
        var appuserId = UUID.randomUUID();

        entityManager.persist(new Note(appuserId, "title 1", "content 1"));
        entityManager.persist(new Note(appuserId, "title 2", "content 2"));

        entityManager.flush();
        entityManager.clear();

        var actual = noteRepository.getByAppuserId(appuserId, 1);

        assertTrue(actual.isEmpty());
    }

    @Test
    void should_find_note_with_id_from_appuser_with_appuserId_when_note_exists() {
        var note = new Note(UUID.randomUUID(), "title", "content");

        entityManager.persist(note);

        entityManager.flush();
        entityManager.clear();

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

        entityManager.persist(note);

        entityManager.flush();
        entityManager.clear();

        noteRepository.deleteById(note.getId());

        var actual = entityManager.find(Note.class, note.getId());
        assertNull(actual);
    }

    @Test
    void should_not_throw_when_trying_to_delete_note_with_id_when_note_does_not_exist() {
        assertDoesNotThrow(() -> {
            noteRepository.deleteById(UUID.randomUUID());
            entityManager.flush();
        });
    }

    @Test
    void should_delete_notes_from_appuser_with_id_when_notes_exist() {
        var appuserId = UUID.randomUUID();
        var note1 = new Note(appuserId, "title", "content");
        var note2 = new Note(appuserId, "title", "content");

        entityManager.persist(note1);
        entityManager.persist(note2);

        entityManager.flush();
        entityManager.clear();

        noteRepository.deleteByAppuserId(appuserId);

        var actual1 = entityManager.find(Note.class, note1.getId());
        var actual2 = entityManager.find(Note.class, note2.getId());
        assertNull(actual1);
        assertNull(actual2);
    }

    @Test
    void should_not_throw_when_trying_to_delete_notes_from_appuser_with_id_and_notes_do_not_exist() {
        assertDoesNotThrow(() -> {
            noteRepository.deleteByAppuserId(UUID.randomUUID());
            entityManager.flush();
        });
    }

    @Test
    void should_save_note_successfully() {
        var note = new Note(UUID.randomUUID(), "title 1", "content 1");

        noteRepository.save(note);

        entityManager.flush();
        entityManager.clear();

        var noteFound = entityManager.find(Note.class, note.getId());
        assertNotNull(noteFound);
    }

    @ParameterizedTest
    @MethodSource
    void should_fail_to_save_note_when_note_data_is_invalid(Note note) {
        assertThrows(Exception.class, () -> {
            noteRepository.save(note);
            entityManager.flush();
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
