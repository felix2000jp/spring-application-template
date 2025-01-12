package dev.felix2000jp.springapplicationtemplate.notes.application;

import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.CreateNoteDto;
import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.NoteDto;
import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.NoteListDto;
import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.UpdateNoteDto;

import java.util.UUID;

public interface NoteService {

    NoteListDto getNotesForCurrentUser(int pageNumber);

    NoteDto getNoteByIdForCurrentUser(UUID id);

    NoteDto createNoteForCurrentUser(CreateNoteDto createNoteDTO);

    NoteDto updateNoteByIdForCurrentUser(UUID noteId, UpdateNoteDto updateNoteDTO);

    NoteDto deleteNoteByIdForCurrentUser(UUID id);

    void deleteNotesByAppuserId(UUID appuserId);

}
