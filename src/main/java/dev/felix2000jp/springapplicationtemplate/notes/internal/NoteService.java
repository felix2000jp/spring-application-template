package dev.felix2000jp.springapplicationtemplate.notes.internal;

import dev.felix2000jp.springapplicationtemplate.appusers.AppuserManagement;
import dev.felix2000jp.springapplicationtemplate.notes.internal.dtos.CreateNoteDto;
import dev.felix2000jp.springapplicationtemplate.notes.internal.dtos.NoteDto;
import dev.felix2000jp.springapplicationtemplate.notes.internal.dtos.NoteListDto;
import dev.felix2000jp.springapplicationtemplate.notes.internal.dtos.UpdateNoteDto;
import dev.felix2000jp.springapplicationtemplate.notes.internal.exceptions.NoteNotFoundException;
import dev.felix2000jp.springapplicationtemplate.shared.AppuserPrincipal;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class NoteService {

    private final NoteMapper noteMapper;
    private final NoteRepository noteRepository;
    private final AppuserManagement appuserManagement;

    public NoteService(NoteMapper noteMapper, NoteRepository noteRepository, AppuserManagement appuserManagement) {
        this.noteMapper = noteMapper;
        this.noteRepository = noteRepository;
        this.appuserManagement = appuserManagement;
    }

    NoteListDto findAll(AppuserPrincipal principal) {
        appuserManagement.verifyAppuserExistsById(principal.id());

        var notes = noteRepository.findByAppuserId(principal.id());

        return noteMapper.toDto(notes);
    }

    NoteDto findById(AppuserPrincipal principal, UUID id) {
        appuserManagement.verifyAppuserExistsById(principal.id());

        var note = noteRepository
                .findByIdAndAppuserId(id, principal.id())
                .orElseThrow(NoteNotFoundException::new);

        return noteMapper.toDto(note);
    }

    NoteDto create(AppuserPrincipal principal, CreateNoteDto createNoteDto) {
        appuserManagement.verifyAppuserExistsById(principal.id());

        var newNote = new Note(createNoteDto.title(), createNoteDto.content(), principal.id());
        var noteSaved = noteRepository.save(newNote);
        return noteMapper.toDto(noteSaved);
    }

    NoteDto update(AppuserPrincipal principal, UUID noteId, UpdateNoteDto updateNoteDto) {
        appuserManagement.verifyAppuserExistsById(principal.id());

        var noteToUpdate = noteRepository
                .findByIdAndAppuserId(noteId, principal.id())
                .orElseThrow(NoteNotFoundException::new);

        noteToUpdate.updateTitleAndContent(updateNoteDto.title(), updateNoteDto.content());
        var noteSaved = noteRepository.save(noteToUpdate);
        return noteMapper.toDto(noteSaved);
    }

    NoteDto delete(AppuserPrincipal principal, UUID id) {
        appuserManagement.verifyAppuserExistsById(principal.id());

        var noteToDelete = noteRepository
                .findByIdAndAppuserId(id, principal.id())
                .orElseThrow(NoteNotFoundException::new);

        noteRepository.delete(noteToDelete);
        return noteMapper.toDto(noteToDelete);
    }

    public void deleteAllByAppuserId(UUID id) {
        noteRepository.deleteAllByAppuserId(id);
    }

}
