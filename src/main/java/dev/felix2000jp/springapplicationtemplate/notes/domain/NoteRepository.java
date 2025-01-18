package dev.felix2000jp.springapplicationtemplate.notes.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NoteRepository {

    List<Note> findAllByAppuserId(UUID appuserId, int pageNumber);

    Optional<Note> findByIdAndAppuserId(UUID id, UUID appuserId);

    void deleteById(UUID id);

    void deleteAllByAppuserId(UUID appuserId);

    void save(Note note);

}
