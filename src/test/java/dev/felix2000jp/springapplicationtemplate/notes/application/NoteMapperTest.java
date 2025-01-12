package dev.felix2000jp.springapplicationtemplate.notes.application;

import dev.felix2000jp.springapplicationtemplate.notes.domain.Note;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NoteMapperTest {

    private final NoteMapper noteMapper = new NoteMapper();

    @Test
    void givenNote_whenToDto_thenReturnNoteDto() {
        // given
        var note = new Note(UUID.randomUUID(), "title", "content");

        // when
        var actual = noteMapper.toDto(note);

        // then
        assertEquals(note.getId(), actual.id());
        assertEquals(note.getTitle(), actual.title());
        assertEquals(note.getContent(), actual.content());
    }

    @Test
    void givenNotes_whenToDto_thenReturnNoteListDto() {
        // given
        var notes = List.of(new Note(UUID.randomUUID(), "title", "content"));

        // when
        var actual = noteMapper.toDto(notes);

        // then
        assertEquals(notes.size(), actual.notes().size());
    }

}
