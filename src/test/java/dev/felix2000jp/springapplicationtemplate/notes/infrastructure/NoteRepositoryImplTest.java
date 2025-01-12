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
    void givenAppuserIdAndPage_whenGetByAppuserId_thenReturnNotes() {
        // given
        var appuserId = note.getAppuserId();
        var page = 0;

        // when
        var actual = noteRepository.getByAppuserId(appuserId, page);

        // then
        assertFalse(actual.isEmpty());
    }

    @Test
    void givenAppuserIdAndEmptyPage_whenGetByAppuserId_thenReturnEmptyList() {
        // given
        var appuserId = note.getAppuserId();
        var page = 1;

        // when
        var actual = noteRepository.getByAppuserId(appuserId, page);

        // then
        assertTrue(actual.isEmpty());

    }

    @Test
    void givenIdAndAppuserId_whenGetByIdAndAppuserId_thenReturnNote() {
        // given
        var id = note.getId();
        var appuserId = note.getAppuserId();

        // when
        var actual = noteRepository.getByIdAndAppuserId(id, appuserId);

        // then
        assertNotNull(actual);
    }

    @Test
    void givenNonExistentIdAndAppuserId_whenGetByIdAndAppuserId_thenReturnNull() {
        // given
        var id = UUID.randomUUID();
        var appuserId = UUID.randomUUID();

        // when
        var actual = noteRepository.getByIdAndAppuserId(id, appuserId);

        // then
        assertNull(actual);
    }

    @Test
    void givenId_whenDeleteById_thenDeleteNote() {
        // given
        var id = note.getId();

        // when
        noteRepository.deleteById(id);
        testEntityManager.flush();
        testEntityManager.clear();

        // then
        assertNull(testEntityManager.find(Note.class, note.getId()));
    }

    @Test
    void givenNonExistentId_deleteById_thenDoesNotThrow() {
        // given
        var nonExistentId = UUID.randomUUID();

        // when and then
        assertDoesNotThrow(() -> {
            noteRepository.deleteById(nonExistentId);
            testEntityManager.flush();
        });
    }

    @Test
    void givenAppuserId_wheDeleteByAppuserId_thenDeleteNotes() {
        // given
        var appuserId = note.getAppuserId();

        // when
        noteRepository.deleteByAppuserId(appuserId);
        testEntityManager.flush();
        testEntityManager.clear();

        // then
        assertNull(testEntityManager.find(Note.class, note.getId()));
    }

    @Test
    void givenNonExistentAppuserId_whenDeleteByAppuserId_thenDoesNotThrow() {
        // given
        var nonExistentAppuserId = UUID.randomUUID();

        // when and then
        assertDoesNotThrow(() -> {
            noteRepository.deleteByAppuserId(nonExistentAppuserId);
            testEntityManager.flush();
        });
    }

    @Test
    void givenNoteToCreate_whenSave_thenSaveNote() {
        // given
        var noteToCreate = new Note(UUID.randomUUID(), "title 1", "content 1");

        // when
        noteRepository.save(noteToCreate);
        testEntityManager.flush();
        testEntityManager.clear();

        // then
        assertNotNull(testEntityManager.find(Note.class, noteToCreate.getId()));
    }

    @ParameterizedTest
    @MethodSource
    void givenInvalidNoteToCreate_whenSave_thenThrow(Note invalidNoteToCreate) {
        // when and then
        assertThrows(Exception.class, () -> {
            noteRepository.save(invalidNoteToCreate);
            testEntityManager.flush();
        });
    }

    private static Stream<Arguments> givenInvalidNoteToCreate_whenSave_thenThrow() {
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
