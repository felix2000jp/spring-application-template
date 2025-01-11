package dev.felix2000jp.springapplicationtemplate.notes.application;

import dev.felix2000jp.springapplicationtemplate.auth.AuthClient;
import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.CreateNoteDTO;
import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.NoteDTO;
import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.NoteListDTO;
import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.UpdateNoteDTO;
import dev.felix2000jp.springapplicationtemplate.notes.domain.Note;
import dev.felix2000jp.springapplicationtemplate.notes.domain.NoteRepository;
import dev.felix2000jp.springapplicationtemplate.notes.domain.exceptions.NoteNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;
    private final NoteMapper noteMapper;
    private final AuthClient authClient;

    NoteServiceImpl(NoteRepository noteRepository, NoteMapper noteMapper, AuthClient authClient) {
        this.noteRepository = noteRepository;
        this.noteMapper = noteMapper;
        this.authClient = authClient;
    }

    @Override
    public NoteListDTO getByAppuser(int pageNumber) {
        var appuserId = authClient.getAuthUser().id();
        var notes = noteRepository.getByAppuserId(appuserId, pageNumber);
        return noteMapper.toDTO(notes);
    }

    @Override
    public NoteDTO getByIdAndAppuser(UUID id) {
        var appuserId = authClient.getAuthUser().id();
        var note = noteRepository.getByIdAndAppuserId(id, appuserId);

        if (note == null) {
            throw new NoteNotFoundException();
        }

        return noteMapper.toDTO(note);
    }

    @Override
    public NoteDTO createByAppuser(CreateNoteDTO createNoteDTO) {
        var appuserId = authClient.getAuthUser().id();

        var noteToCreate = new Note(appuserId, createNoteDTO.title(), createNoteDTO.content());
        noteRepository.save(noteToCreate);

        return noteMapper.toDTO(noteToCreate);
    }

    @Override
    public NoteDTO updateByIdAndAppuser(UUID noteId, UpdateNoteDTO updateNoteDTO) {
        var appuserId = authClient.getAuthUser().id();

        var noteToUpdate = noteRepository.getByIdAndAppuserId(noteId, appuserId);

        if (noteToUpdate == null) {
            throw new NoteNotFoundException();
        }

        noteToUpdate.setTitle(updateNoteDTO.title());
        noteToUpdate.setContent(updateNoteDTO.content());
        noteRepository.save(noteToUpdate);

        return noteMapper.toDTO(noteToUpdate);
    }

    @Override
    public NoteDTO deleteByIdAndAppuser(UUID id) {
        var appuserId = authClient.getAuthUser().id();
        var noteToDelete = noteRepository.getByIdAndAppuserId(id, appuserId);

        if (noteToDelete == null) {
            throw new NoteNotFoundException();
        }

        noteRepository.deleteById(noteToDelete.getId());
        return noteMapper.toDTO(noteToDelete);
    }

    @Override
    public void deleteByAppuserId(UUID appuserId) {
        noteRepository.deleteByAppuserId(appuserId);
    }

}
