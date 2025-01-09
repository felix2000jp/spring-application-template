package dev.felix2000jp.springapplicationtemplate.notes.application;

import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.NoteDTO;
import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.NoteListDTO;
import dev.felix2000jp.springapplicationtemplate.notes.domain.Note;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class NoteMapper {

    NoteDTO toDTO(Note note) {
        return new NoteDTO(note.getId(), note.getTitle(), note.getContent());
    }

    NoteListDTO toDTO(List<Note> notes) {
        return new NoteListDTO(notes.stream().map(this::toDTO).toList());
    }

}
