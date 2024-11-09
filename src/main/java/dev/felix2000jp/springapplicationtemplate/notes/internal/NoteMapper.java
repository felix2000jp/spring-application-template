package dev.felix2000jp.springapplicationtemplate.notes.internal;

import dev.felix2000jp.springapplicationtemplate.notes.internal.dtos.NoteDTO;
import dev.felix2000jp.springapplicationtemplate.notes.internal.dtos.NoteListDTO;
import org.mapstruct.Mapper;

import java.util.Collection;

@Mapper(componentModel = "spring")
interface NoteMapper {

    NoteDTO toDTO(Note note);

    default NoteListDTO toDTO(Collection<Note> notes) {
        var noteDTOs = notes.stream().map(this::toDTO).toList();
        return new NoteListDTO(noteDTOs);
    }

}
