package dev.felix2000jp.springapplicationtemplate.notes.application;

import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.CreateNoteDto;
import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.NoteDto;
import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.NoteListDto;
import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.UpdateNoteDto;
import dev.felix2000jp.springapplicationtemplate.notes.domain.Note;
import dev.felix2000jp.springapplicationtemplate.notes.domain.NoteRepository;
import dev.felix2000jp.springapplicationtemplate.notes.domain.exceptions.NoteNotFoundException;
import dev.felix2000jp.springapplicationtemplate.shared.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class NoteService {

    private static final Logger log = LoggerFactory.getLogger(NoteService.class);

    private final NoteRepository noteRepository;
    private final NoteMapper noteMapper;
    private final SecurityService securityService;

    NoteService(NoteRepository noteRepository, NoteMapper noteMapper, SecurityService securityService) {
        this.noteRepository = noteRepository;
        this.noteMapper = noteMapper;
        this.securityService = securityService;
    }

    public NoteListDto getNotesForCurrentUser(int pageNumber) {
        var appuserId = securityService.getUser().id();
        var notes = noteRepository.findAllByAppuserId(appuserId, pageNumber);
        return noteMapper.toDto(notes);
    }

    public NoteDto getNoteByIdForCurrentUser(UUID id) {
        var appuserId = securityService.getUser().id();

        var note = noteRepository
                .findByIdAndAppuserId(id, appuserId)
                .orElseThrow(NoteNotFoundException::new);

        return noteMapper.toDto(note);
    }

    public NoteDto createNoteForCurrentUser(CreateNoteDto createNoteDto) {
        var appuserId = securityService.getUser().id();

        var noteToCreate = new Note(appuserId, createNoteDto.title(), createNoteDto.content());
        noteRepository.save(noteToCreate);
        log.info("Note with id {} created", noteToCreate.getId());

        return noteMapper.toDto(noteToCreate);
    }

    public NoteDto updateNoteByIdForCurrentUser(UUID noteId, UpdateNoteDto updateNoteDto) {
        var appuserId = securityService.getUser().id();

        var noteToUpdate = noteRepository
                .findByIdAndAppuserId(noteId, appuserId)
                .orElseThrow(NoteNotFoundException::new);

        noteToUpdate.setTitle(updateNoteDto.title());
        noteToUpdate.setContent(updateNoteDto.content());
        noteRepository.save(noteToUpdate);
        log.info("Note with id {} updated", noteToUpdate.getId());

        return noteMapper.toDto(noteToUpdate);
    }

    public NoteDto deleteNoteByIdForCurrentUser(UUID id) {
        var appuserId = securityService.getUser().id();

        var noteToDelete = noteRepository
                .findByIdAndAppuserId(id, appuserId)
                .orElseThrow(NoteNotFoundException::new);

        noteRepository.deleteById(noteToDelete.getId());
        log.info("Note with id {} deleted", noteToDelete.getId());

        return noteMapper.toDto(noteToDelete);
    }

    public void deleteNotesByAppuserId(UUID appuserId) {
        noteRepository.deleteAllByAppuserId(appuserId);
        log.info("Notes with appuserId {} deleted", appuserId);
    }

}
