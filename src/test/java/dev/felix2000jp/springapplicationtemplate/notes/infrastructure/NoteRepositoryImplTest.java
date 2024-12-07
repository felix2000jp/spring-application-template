package dev.felix2000jp.springapplicationtemplate.notes.infrastructure;

import dev.felix2000jp.springapplicationtemplate.notes.domain.Note;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Testcontainers
@Import(NoteRepositoryImpl.class)
class NoteRepositoryImplTest {

    @Container
    @ServiceConnection
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres");

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private NoteRepositoryImpl noteRepository;

    @Test
    @Transactional
    void should_find_notes_from_appuser_with_appuserId_when_page_is_not_empty() {
        var appuserId = UUID.randomUUID();

        entityManager.persistAndFlush(new Note(appuserId, "title 1", "content 1"));
        entityManager.persistAndFlush(new Note(appuserId, "title 2", "content 2"));

        var actual = noteRepository.getByAppuserId(appuserId, 0);

        assertFalse(actual.isEmpty());
    }

    @Test
    @Transactional
    void should_not_find_notes_from_appuser_with_appuserId_when_page_is_empty() {
        var appuserId = UUID.randomUUID();

        entityManager.persistAndFlush(new Note(appuserId, "title 1", "content 1"));
        entityManager.persistAndFlush(new Note(appuserId, "title 2", "content 2"));

        var actual = noteRepository.getByAppuserId(appuserId, 1);

        assertTrue(actual.isEmpty());
    }

    @Test
    @Transactional
    void should_find_note_with_id_from_appuser_with_appuserId_when_note_exists() {
        var note = new Note(UUID.randomUUID(), "title", "content");

        entityManager.persistAndFlush(note);

        var actual = noteRepository.getByIdAndAppuserId(note.getId(), note.getAppuserId());

        assertNotNull(actual);
    }

    @Test
    @Transactional
    void should_not_find_note_with_id_from_appuser_with_appuserId_when_note_does_not_exist() {
        var note = new Note(UUID.randomUUID(), "title", "content");

        var actual = noteRepository.getByIdAndAppuserId(note.getId(), note.getAppuserId());

        assertNull(actual);
    }

    @Test
    @Transactional
    void should_delete_note_with_id_when_note_exists() {
        var note = new Note(UUID.randomUUID(), "title", "content");
        entityManager.persistAndFlush(note);

        noteRepository.deleteById(note.getId());

        var actual = entityManager.find(Note.class, note.getId());
        assertNull(actual);
    }

    @Test
    @Transactional
    void should_not_throw_when_trying_to_delete_note_with_id_when_note_does_not_exist() {
        assertDoesNotThrow(() -> noteRepository.deleteById(UUID.randomUUID()));
    }

    @Test
    @Transactional
    void should_delete_notes_from_appuser_with_id_when_notes_exist() {
        var appuserId = UUID.randomUUID();
        var note1 = new Note(appuserId, "title", "content");
        var note2 = new Note(appuserId, "title", "content");

        entityManager.persistAndFlush(note1);
        entityManager.persistAndFlush(note2);

        noteRepository.deleteByAppuserId(appuserId);

        var actual1 = entityManager.find(Note.class, note1.getId());
        var actual2 = entityManager.find(Note.class, note2.getId());
        assertNull(actual1);
        assertNull(actual2);
    }

    @Test
    @Transactional
    void should_not_throw_when_trying_to_delete_notes_from_appuser_with_id_and_notes_do_not_exist() {
        assertDoesNotThrow(() -> noteRepository.deleteByAppuserId(UUID.randomUUID()));
    }

    @Test
    @Transactional
    void should_save_note_successfully() {
        var note = new Note(UUID.randomUUID(), "title 1", "content 1");

        noteRepository.save(note);

        var noteFound = entityManager.find(Note.class, note.getId());
        assertNotNull(noteFound);
    }

}
