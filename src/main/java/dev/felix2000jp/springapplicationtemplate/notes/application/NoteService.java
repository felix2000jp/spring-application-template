package dev.felix2000jp.springapplicationtemplate.notes.application;

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
public class NoteService {

    private final NoteRepository noteRepository;
    private final NoteMapper noteMapper;

    NoteService(NoteRepository noteRepository, NoteMapper noteMapper) {
        this.noteRepository = noteRepository;
        this.noteMapper = noteMapper;
    }

    public NoteListDTO getByAppuser(int pageNumber) {
        var appuserId = UUID.randomUUID();
        var notes = noteRepository.getByAppuserId(appuserId, pageNumber);
        return noteMapper.toDTO(notes);
    }

    public NoteDTO getByIdAndAppuser(UUID id) {
        var appuserId = UUID.randomUUID();
        var note = noteRepository.getByIdAndAppuserId(id, appuserId);

        if (note == null) {
            throw new NoteNotFoundException();
        }

        return noteMapper.toDTO(note);
    }

    public NoteDTO createByAppuser(CreateNoteDTO createNoteDTO) {
        var appuserId = UUID.randomUUID();

        var noteToCreate = new Note(appuserId, createNoteDTO.title(), createNoteDTO.content());
        noteRepository.save(noteToCreate);

        return noteMapper.toDTO(noteToCreate);
    }

    public NoteDTO updateByAppuser(UUID noteId, UpdateNoteDTO updateNoteDTO) {
        var appuserId = UUID.randomUUID();

        var noteToUpdate = noteRepository.getByIdAndAppuserId(noteId, appuserId);

        if (noteToUpdate == null) {
            throw new NoteNotFoundException();
        }

        noteToUpdate.setTitle(updateNoteDTO.title());
        noteToUpdate.setContent(updateNoteDTO.content());
        noteRepository.save(noteToUpdate);

        return noteMapper.toDTO(noteToUpdate);
    }

    public NoteDTO deleteByIdAndAppuser(UUID id) {
        var appuserId = UUID.randomUUID();
        var noteToDelete = noteRepository.getByIdAndAppuserId(id, appuserId);

        if (noteToDelete == null) {
            throw new NoteNotFoundException();
        }

        noteRepository.deleteById(noteToDelete.getId());
        return noteMapper.toDTO(noteToDelete);
    }

    public void deleteByAppuserId(UUID appuserId) {
        noteRepository.deleteByAppuserId(appuserId);
    }

}
