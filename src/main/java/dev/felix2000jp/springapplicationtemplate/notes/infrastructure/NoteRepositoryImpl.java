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
    private final NoteJpaRepository noteJpaRepository;

    NoteRepositoryImpl(NoteJpaRepository noteJpaRepository) {
        this.noteJpaRepository = noteJpaRepository;
    }

    @Override
    public List<Note> getByAppuserId(UUID appuserId, int pageNumber) {
        var pageable = PageRequest.of(pageNumber, PAGE_SIZE);
        var page = noteJpaRepository.findByAppuserId(appuserId, pageable);
        return page.getContent();
    }

    @Override
    public Note getByIdAndAppuserId(UUID id, UUID appuserId) {
        var optionalNote = noteJpaRepository.findByIdAndAppuserId(id, appuserId);
        return optionalNote.orElse(null);
    }

    @Override
    public void deleteById(UUID id) {
        noteJpaRepository.deleteById(id);
    }

    @Override
    public void deleteByAppuserId(UUID appuserId) {
        noteJpaRepository.deleteAllByAppuserId(appuserId);
    }

    @Override
    public void save(Note note) {
        noteJpaRepository.save(note);
    }

}
