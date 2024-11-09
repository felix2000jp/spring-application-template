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

    NoteListDTO findAll() {
        var appuserDTO = appuserManagement.getAuthenticatedAppuserDTO();

        var notes = noteRepository.findByAppuserId(appuserDTO.id());

        return noteMapper.toDTO(notes);
    }

    NoteDTO find(UUID id) {
        var appuserDTO = appuserManagement.getAuthenticatedAppuserDTO();

        var note = noteRepository
                .findByIdAndAppuserId(id, appuserDTO.id())
                .orElseThrow(NoteNotFoundException::new);

        return noteMapper.toDTO(note);
    }

    NoteDTO create(CreateNoteDTO createNoteDTO) {
        var appuserDTO = appuserManagement.verifyAuthenticatedAppuserDTO();

        var newNote = new Note(createNoteDTO.title(), createNoteDTO.content(), appuserDTO.id());
        var noteSaved = noteRepository.save(newNote);
        return noteMapper.toDTO(noteSaved);
    }

    NoteDTO update(UUID noteId, UpdateNoteDTO updateNoteDTO) {
        var appuserDTO = appuserManagement.verifyAuthenticatedAppuserDTO();

        var noteToUpdate = noteRepository
                .findByIdAndAppuserId(noteId, appuserDTO.id())
                .orElseThrow(NoteNotFoundException::new);

        noteToUpdate.updateTitleAndContent(updateNoteDTO.title(), updateNoteDTO.content());
        var noteSaved = noteRepository.save(noteToUpdate);
        return noteMapper.toDTO(noteSaved);
    }

    NoteDTO delete(UUID id) {
        var appuserDTO = appuserManagement.verifyAuthenticatedAppuserDTO();

        var noteToDelete = noteRepository
                .findByIdAndAppuserId(id, appuserDTO.id())
                .orElseThrow(NoteNotFoundException::new);

        noteRepository.delete(noteToDelete);
        return noteMapper.toDTO(noteToDelete);
    }

    public void deleteAllByAppuserId(UUID id) {
        noteRepository.deleteAllByAppuserId(id);
    }

}
