package dev.felix2000jp.springapplicationtemplate.notes.infrastructure.database;

import dev.felix2000jp.springapplicationtemplate.notes.domain.Note;
import dev.felix2000jp.springapplicationtemplate.notes.domain.NoteRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
class NoteRepositoryImpl implements NoteRepository {

    private static final int PAGE_SIZE = 50;

    private final NoteJpaRepository noteJpaRepository;

    NoteRepositoryImpl(NoteJpaRepository noteJpaRepository) {
        this.noteJpaRepository = noteJpaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Note> findAllByAppuserId(UUID appuserId, int pageNumber) {
        var pageable = PageRequest.of(pageNumber, PAGE_SIZE);
        var page = noteJpaRepository.findByAppuserId(appuserId, pageable);
        return page.getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Note> findByIdAndAppuserId(UUID id, UUID appuserId) {
        return noteJpaRepository.findByIdAndAppuserId(id, appuserId);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        noteJpaRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteAllByAppuserId(UUID appuserId) {
        noteJpaRepository.deleteAllByAppuserId(appuserId);
    }

    @Override
    @Transactional
    public void save(Note note) {
        noteJpaRepository.save(note);
    }

}
