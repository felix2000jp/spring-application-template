package dev.felix2000jp.springapplicationtemplate.notes.application;

import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.NoteDto;
import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.NoteListDto;
import dev.felix2000jp.springapplicationtemplate.notes.domain.Note;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class NoteMapper {

    NoteDto toDTO(Note note) {
        return new NoteDto(note.getId(), note.getTitle(), note.getContent());
    }

    NoteListDto toDTO(List<Note> notes) {
        return new NoteListDto(notes.stream().map(this::toDTO).toList());
    }

}
