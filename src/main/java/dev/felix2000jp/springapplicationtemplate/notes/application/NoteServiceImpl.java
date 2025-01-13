package dev.felix2000jp.springapplicationtemplate.notes.application;

import dev.felix2000jp.springapplicationtemplate.shared.SecurityService;
import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.CreateNoteDto;
import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.NoteDto;
import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.NoteListDto;
import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.UpdateNoteDto;
import dev.felix2000jp.springapplicationtemplate.notes.domain.Note;
import dev.felix2000jp.springapplicationtemplate.notes.domain.NoteRepository;
import dev.felix2000jp.springapplicationtemplate.notes.domain.exceptions.NoteNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;
    private final NoteMapper noteMapper;
    private final SecurityService securityService;

    NoteServiceImpl(NoteRepository noteRepository, NoteMapper noteMapper, SecurityService securityService) {
        this.noteRepository = noteRepository;
        this.noteMapper = noteMapper;
        this.securityService = securityService;
    }

    @Override
    public NoteListDto getNotesForCurrentUser(int pageNumber) {
        var appuserId = securityService.getUser().id();
        var notes = noteRepository.findAllByAppuserId(appuserId, pageNumber);
        return noteMapper.toDto(notes);
    }

    @Override
    public NoteDto getNoteByIdForCurrentUser(UUID id) {
        var appuserId = securityService.getUser().id();

        var note = noteRepository.findByIdAndAppuserId(id, appuserId);
        if (note == null) {
            throw new NoteNotFoundException();
        }

        return noteMapper.toDto(note);
    }

    @Override
    public NoteDto createNoteForCurrentUser(CreateNoteDto createNoteDto) {
        var appuserId = securityService.getUser().id();

        var noteToCreate = new Note(appuserId, createNoteDto.title(), createNoteDto.content());
        noteRepository.save(noteToCreate);

        return noteMapper.toDto(noteToCreate);
    }

    @Override
    public NoteDto updateNoteByIdForCurrentUser(UUID noteId, UpdateNoteDto updateNoteDto) {
        var appuserId = securityService.getUser().id();

        var noteToUpdate = noteRepository.findByIdAndAppuserId(noteId, appuserId);
        if (noteToUpdate == null) {
            throw new NoteNotFoundException();
        }

        noteToUpdate.setTitle(updateNoteDto.title());
        noteToUpdate.setContent(updateNoteDto.content());
        noteRepository.save(noteToUpdate);

        return noteMapper.toDto(noteToUpdate);
    }

    @Override
    public NoteDto deleteNoteByIdForCurrentUser(UUID id) {
        var appuserId = securityService.getUser().id();

        var noteToDelete = noteRepository.findByIdAndAppuserId(id, appuserId);
        if (noteToDelete == null) {
            throw new NoteNotFoundException();
        }

        noteRepository.deleteById(noteToDelete.getId());
        return noteMapper.toDto(noteToDelete);
    }

    @Override
    public void deleteNotesByAppuserId(UUID appuserId) {
        noteRepository.deleteAllByAppuserId(appuserId);
    }

}
