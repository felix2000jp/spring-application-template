package dev.felix2000jp.springapplicationtemplate.notes.application;

import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.CreateNoteDto;
import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.NoteDto;
import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.NoteListDto;
import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.UpdateNoteDto;

import java.util.UUID;

public interface NoteService {

    NoteListDto getByAppuser(int pageNumber);

    NoteDto getByIdAndAppuser(UUID id);

    NoteDto createByAppuser(CreateNoteDto createNoteDTO);

    NoteDto updateByIdAndAppuser(UUID noteId, UpdateNoteDto updateNoteDTO);

    NoteDto deleteByIdAndAppuser(UUID id);

    void deleteByAppuserId(UUID appuserId);

}
