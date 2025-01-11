package dev.felix2000jp.springapplicationtemplate.notes.application;

import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.CreateNoteDTO;
import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.NoteDTO;
import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.NoteListDTO;
import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.UpdateNoteDTO;

import java.util.UUID;

public interface NoteService {

    NoteListDTO getByAppuser(int pageNumber);

    NoteDTO getByIdAndAppuser(UUID id);

    NoteDTO createByAppuser(CreateNoteDTO createNoteDTO);

    NoteDTO updateByIdAndAppuser(UUID noteId, UpdateNoteDTO updateNoteDTO);

    NoteDTO deleteByIdAndAppuser(UUID id);

    void deleteByAppuserId(UUID appuserId);

}
