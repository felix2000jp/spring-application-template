package dev.felix2000jp.springapplicationtemplate.notes.application;

import dev.felix2000jp.springapplicationtemplate.notes.domain.Note;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class NoteMapperTest {

    private final NoteMapper noteMapper = new NoteMapper();

    @Test
    void toDto_given_note_then_map_to_dto() {
        var note = new Note(UUID.randomUUID(), "title", "content");

        var actual = noteMapper.toDto(note);

        assertThat(actual.id()).isEqualTo(note.getId());
        assertThat(actual.title()).isEqualTo(note.getTitle());
        assertThat(actual.content()).isEqualTo(note.getContent());
    }

    @Test
    void toDto_given_list_of_notes_then_map_to_list_dto() {
        var notes = List.of(new Note(UUID.randomUUID(), "title", "content"));

        var actual = noteMapper.toDto(notes);

        assertThat(actual.notes()).hasSameSizeAs(notes);
    }

}
