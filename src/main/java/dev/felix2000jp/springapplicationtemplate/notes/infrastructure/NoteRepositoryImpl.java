package dev.felix2000jp.springapplicationtemplate.notes.infrastructure;

import dev.felix2000jp.springapplicationtemplate.notes.domain.Note;
import dev.felix2000jp.springapplicationtemplate.notes.domain.NoteRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
class NoteRepositoryImpl implements NoteRepository {

    private static final int PAGE_SIZE = 50;

    private final NoteJPARepository noteJPARepository;

    NoteRepositoryImpl(NoteJPARepository noteJPARepository) {
        this.noteJPARepository = noteJPARepository;
    }

    @Override
    public List<Note> getByAppuserId(UUID appuserId, int pageNumber) {
        var pageable = PageRequest.of(pageNumber, PAGE_SIZE);
        var page = noteJPARepository.findByAppuserId(appuserId, pageable);
        return page.getContent();
    }

    @Override
    public Note getByIdAndAppuserId(UUID id, UUID appuserId) {
        var optionalNote = noteJPARepository.findByIdAndAppuserId(id, appuserId);
        return optionalNote.orElse(null);
    }

    @Override
    public void deleteById(UUID id) {
        noteJPARepository.deleteById(id);
    }

    @Override
    public void deleteByAppuserId(UUID appuserId) {
        noteJPARepository.deleteAllByAppuserId(appuserId);
    }

    @Override
    public void save(Note note) {
        noteJPARepository.save(note);
    }

}
