package dev.felix2000jp.springapplicationtemplate.notes.internal;

import dev.felix2000jp.springapplicationtemplate.appusers.AppuserManagement;
import dev.felix2000jp.springapplicationtemplate.notes.internal.dtos.CreateNoteDto;
import dev.felix2000jp.springapplicationtemplate.notes.internal.dtos.NoteDto;
import dev.felix2000jp.springapplicationtemplate.notes.internal.dtos.NoteListDto;
import dev.felix2000jp.springapplicationtemplate.notes.internal.dtos.UpdateNoteDto;
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

    NoteListDto findAll() {
        var appuserDTO = appuserManagement.getAuthenticatedAppuserDTO();

        var notes = noteRepository.findByAppuserId(appuserDTO.id());

        return noteMapper.toDto(notes);
    }

    NoteDto find(UUID id) {
        var appuserDTO = appuserManagement.getAuthenticatedAppuserDTO();

        var note = noteRepository
                .findByIdAndAppuserId(id, appuserDTO.id())
                .orElseThrow(NoteNotFoundException::new);

        return noteMapper.toDto(note);
    }

    NoteDto create(CreateNoteDto createNoteDto) {
        var appuserDTO = appuserManagement.verifyAuthenticatedAppuserDTO();

        var newNote = new Note(createNoteDto.title(), createNoteDto.content(), appuserDTO.id());
        var noteSaved = noteRepository.save(newNote);
        return noteMapper.toDto(noteSaved);
    }

    NoteDto update(UUID noteId, UpdateNoteDto updateNoteDto) {
        var appuserDTO = appuserManagement.verifyAuthenticatedAppuserDTO();

        var noteToUpdate = noteRepository
                .findByIdAndAppuserId(noteId, appuserDTO.id())
                .orElseThrow(NoteNotFoundException::new);

        noteToUpdate.updateTitleAndContent(updateNoteDto.title(), updateNoteDto.content());
        var noteSaved = noteRepository.save(noteToUpdate);
        return noteMapper.toDto(noteSaved);
    }

    NoteDto delete(UUID id) {
        var appuserDTO = appuserManagement.verifyAuthenticatedAppuserDTO();

        var noteToDelete = noteRepository
                .findByIdAndAppuserId(id, appuserDTO.id())
                .orElseThrow(NoteNotFoundException::new);

        noteRepository.delete(noteToDelete);
        return noteMapper.toDto(noteToDelete);
    }

    public void deleteAllByAppuserId(UUID id) {
        noteRepository.deleteAllByAppuserId(id);
    }

}
