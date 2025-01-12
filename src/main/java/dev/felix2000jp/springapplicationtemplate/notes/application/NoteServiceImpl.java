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
    public NoteListDto getByAppuser(int pageNumber) {
        var appuserId = securityService.getUser().id();
        var notes = noteRepository.getByAppuserId(appuserId, pageNumber);
        return noteMapper.toDTO(notes);
    }

    @Override
    public NoteDto getByIdAndAppuser(UUID id) {
        var appuserId = securityService.getUser().id();

        var note = noteRepository.getByIdAndAppuserId(id, appuserId);
        if (note == null) {
            throw new NoteNotFoundException();
        }

        return noteMapper.toDTO(note);
    }

    @Override
    public NoteDto createByAppuser(CreateNoteDto createNoteDTO) {
        var appuserId = securityService.getUser().id();

        var noteToCreate = new Note(appuserId, createNoteDTO.title(), createNoteDTO.content());
        noteRepository.save(noteToCreate);

        return noteMapper.toDTO(noteToCreate);
    }

    @Override
    public NoteDto updateByIdAndAppuser(UUID noteId, UpdateNoteDto updateNoteDTO) {
        var appuserId = securityService.getUser().id();

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
    public NoteDto deleteByIdAndAppuser(UUID id) {
        var appuserId = securityService.getUser().id();

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
