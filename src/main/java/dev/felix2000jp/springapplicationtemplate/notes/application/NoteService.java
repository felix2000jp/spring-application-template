package dev.felix2000jp.springapplicationtemplate.notes.application;

import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.CreateNoteDto;
import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.NoteDto;
import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.NoteListDto;
import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.UpdateNoteDto;

import java.util.UUID;

public interface NoteService {

    NoteListDto getByCurrentUser(int pageNumber);

    NoteDto getByIdAndCurrentUser(UUID id);

    NoteDto createByCurrentUser(CreateNoteDto createNoteDTO);

    NoteDto updateByIdAndCurrentUser(UUID noteId, UpdateNoteDto updateNoteDTO);

    NoteDto deleteByIdAndCurrentUser(UUID id);

    void deleteByAppuserId(UUID appuserId);

}
