package dev.felix2000jp.springapplicationtemplate.notes.internal;

import dev.felix2000jp.springapplicationtemplate.TestcontainersConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class NoteRepositoryTest {

    @Autowired
    private NoteRepository noteRepository;

    private Note note;

    @BeforeEach
    void setUp() {
        note = new Note("title", "content", UUID.randomUUID());
        noteRepository.save(note);
    }

    @AfterEach
    void tearDown() {
        noteRepository.delete(note);
    }

    @Test
    void findByAppuserId_should_return_notes_when_notes_are_found() {
        var actual = noteRepository.findByAppuserId(note.getAppuserId());

        assertThat(actual).usingRecursiveComparison().isEqualTo(List.of(note));
    }

    @Test
    void findByAppuserId_should_return_empty_when_title_not_exists() {
        var actual = noteRepository.findByAppuserId(UUID.randomUUID());

        assertThat(actual).usingRecursiveComparison().isEqualTo(List.of());
    }

    @Test
    void findByIdAndAppuserId_should_return_notes_when_notes_are_found() {
        var actual = noteRepository.findByIdAndAppuserId(note.getId(), note.getAppuserId());

        assertThat(actual).usingRecursiveComparison().isEqualTo(Optional.of(note));
    }

    @Test
    void findByIdAndAppuserId_should_return_empty_when_title_not_exists() {
        var actual = noteRepository.findByIdAndAppuserId(UUID.randomUUID(), note.getAppuserId());

        assertThat(actual).usingRecursiveComparison().isEqualTo(Optional.empty());
    }

    @Test
    void deleteAllByAppuserId_should_delete_all_notes_when_notes_are_found_by_appuserId() {
        noteRepository.deleteAllByAppuserId(note.getAppuserId());

        var expected = noteRepository.findById(note.getId());

        assertThat(expected).isEmpty();
    }

    @Test
    void deleteAllByAppuserId_should_delete_no_notes_when_notes_are_not_found_by_appuserId() {
        noteRepository.deleteAllByAppuserId(UUID.randomUUID());

        var expected = noteRepository.findById(note.getId());

        assertThat(expected).isNotNull();
    }

}