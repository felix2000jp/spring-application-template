package dev.felix2000jp.springapplicationtemplate.notes.application;

import dev.felix2000jp.springapplicationtemplate.notes.domain.Note;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NoteMapperTest {

    private final NoteMapper noteMapper = new NoteMapper();

    @Test
    void should_map_note_to_noteDTO_successfully() {
        var note = new Note(UUID.randomUUID(), "title", "content");

        var actual = noteMapper.toDTO(note);

        assertEquals(note.getId(), actual.id());
        assertEquals(note.getTitle(), actual.title());
        assertEquals(note.getContent(), actual.content());
    }

    @Test
    void should_map_notes_to_noteListDTO_successfully() {
        var notes = List.of(new Note(UUID.randomUUID(), "title", "content"));

        var actual = noteMapper.toDTO(notes);

        assertEquals(notes.size(), actual.notes().size());
    }

}
