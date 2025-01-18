package dev.felix2000jp.springapplicationtemplate.notes.infrastructure;

import dev.felix2000jp.springapplicationtemplate.notes.domain.Note;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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

import static org.assertj.core.api.Assertions.*;
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

    private Note note;

    @BeforeEach
    void setUp() {
        note = new Note(UUID.randomUUID(), "title", "content");
        testEntityManager.persistAndFlush(note);

        testEntityManager.clear();
    }

    @AfterEach
    void tearDown() {
        testEntityManager.clear();

        testEntityManager
                .getEntityManager()
                .createNativeQuery("TRUNCATE TABLE note")
                .executeUpdate();
    }

    @Test
    void findAllByAppuserId_given_appuser_id_and_page_then_return_notes() {
        var actual = noteRepository.findAllByAppuserId(note.getAppuserId(), 0);

        assertThat(actual).isNotEmpty();
    }

    @Test
    void findAllByAppuserId_given_appuser_id_and_empty_page_then_return_empty_list() {
        var actual = noteRepository.findAllByAppuserId(note.getAppuserId(), 1);

        assertThat(actual).isEmpty();
    }

    @Test
    void findByIdAndAppuserId_given_id_and_appuser_id_then_return_note() {
        var actual = noteRepository.findByIdAndAppuserId(note.getId(), note.getAppuserId());

        assertThat(actual).isPresent();
    }

    @Test
    void findByIdAndAppuserId_given_not_found_id_and_not_found_appuser_id_then_return_empty_optional() {
        var actual = noteRepository.findByIdAndAppuserId(UUID.randomUUID(), UUID.randomUUID());

        assertThat(actual).isNotPresent();
    }

    @Test
    void deleteById_given_note_id_then_delete_note() {
        noteRepository.deleteById(note.getId());

        testEntityManager.flush();
        testEntityManager.clear();

        var deletedNote = testEntityManager.find(Note.class, note.getId());
        assertThat(deletedNote).isNull();
    }

    @Test
    void deleteById_given_not_found_id_then_fail_without_throwing() {
        assertThatCode(() -> {
            noteRepository.deleteById(UUID.randomUUID());
            testEntityManager.flush();
        }).doesNotThrowAnyException();
    }

    @Test
    void deleteAllByAppuserId_given_appuser_id_then_delete_all_notes_with_appuser_id() {
        noteRepository.deleteAllByAppuserId(note.getAppuserId());
        testEntityManager.flush();
        testEntityManager.clear();

        var deletedNote = testEntityManager.find(Note.class, note.getId());
        assertThat(deletedNote).isNull();
    }

    @Test
    void deleteAllByAppuserId_given_not_found_appuser_id_then_fail_without_throwing() {
        assertThatCode(() -> {
            noteRepository.deleteAllByAppuserId(UUID.randomUUID());
            testEntityManager.flush();
        }).doesNotThrowAnyException();
    }

    @Test
    void save_given_note_to_create_then_save_note_to_database() {
        var noteToCreate = new Note(UUID.randomUUID(), "title 1", "content 1");

        noteRepository.save(noteToCreate);
        testEntityManager.flush();
        testEntityManager.clear();

        var createdNote = testEntityManager.find(Note.class, noteToCreate.getId());
        assertThat(createdNote).isNotNull();
    }

    @ParameterizedTest
    @MethodSource
    void save_given_invalid_note_to_create_then_throw_exception(Note invalidNoteToCreate) {
        assertThatThrownBy(() -> {
            noteRepository.save(invalidNoteToCreate);
            testEntityManager.flush();
        }).isInstanceOf(Exception.class);
    }

    private static Stream<Arguments> save_given_invalid_note_to_create_then_throw_exception() {
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
