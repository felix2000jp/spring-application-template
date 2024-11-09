package dev.felix2000jp.springapplicationtemplate.notes.internal;

import dev.felix2000jp.springapplicationtemplate.appusers.AppuserManagement;
import dev.felix2000jp.springapplicationtemplate.notes.internal.dtos.CreateNoteDTO;
import dev.felix2000jp.springapplicationtemplate.notes.internal.dtos.NoteDTO;
import dev.felix2000jp.springapplicationtemplate.notes.internal.dtos.NoteListDTO;
import dev.felix2000jp.springapplicationtemplate.notes.internal.dtos.UpdateNoteDTO;
import dev.felix2000jp.springapplicationtemplate.notes.internal.exceptions.NoteNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class NoteService {

    private final NoteMapper noteMapper;
    private final NoteRepository noteRepository;
    private final AppuserManagement appuserManagement;

    NoteService(NoteMapper noteMapper, NoteRepository noteRepository, AppuserManagement appuserManagement) {
        this.noteMapper = noteMapper;
        this.noteRepository = noteRepository;
        this.appuserManagement = appuserManagement;
    }

    public void deleteAllByAppuserId(UUID id) {
        noteRepository.deleteAllByAppuserId(id);
    }

    NoteListDTO findAll() {
        var authenticatedAppuser = appuserManagement.getAuthenticatedAppuser();

        var notes = noteRepository.findByAppuserId(authenticatedAppuser.id());

        return noteMapper.toDTO(notes);
    }

    NoteDTO find(UUID id) {
        var authenticatedAppuser = appuserManagement.getAuthenticatedAppuser();

        var note = noteRepository
                .findByIdAndAppuserId(id, authenticatedAppuser.id())
                .orElseThrow(NoteNotFoundException::new);

        return noteMapper.toDTO(note);
    }

    NoteDTO create(CreateNoteDTO createNoteDTO) {
        var authenticatedAppuser = appuserManagement.verifyAuthenticatedAppuser();

        var newNote = new Note(createNoteDTO.title(), createNoteDTO.content(), authenticatedAppuser.id());
        var noteSaved = noteRepository.save(newNote);
        return noteMapper.toDTO(noteSaved);
    }

    NoteDTO update(UUID noteId, UpdateNoteDTO updateNoteDTO) {
        var authenticatedAppuser = appuserManagement.verifyAuthenticatedAppuser();

        var noteToUpdate = noteRepository
                .findByIdAndAppuserId(noteId, authenticatedAppuser.id())
                .orElseThrow(NoteNotFoundException::new);

        noteToUpdate.updateTitleAndContent(updateNoteDTO.title(), updateNoteDTO.content());
        var noteSaved = noteRepository.save(noteToUpdate);
        return noteMapper.toDTO(noteSaved);
    }

    NoteDTO delete(UUID id) {
        var authenticatedAppuser = appuserManagement.verifyAuthenticatedAppuser();

        var noteToDelete = noteRepository
                .findByIdAndAppuserId(id, authenticatedAppuser.id())
                .orElseThrow(NoteNotFoundException::new);

        noteRepository.delete(noteToDelete);
        return noteMapper.toDTO(noteToDelete);
    }

}
