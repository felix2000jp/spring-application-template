package dev.felix2000jp.springapplicationtemplate.notes.domain;

import java.util.List;
import java.util.UUID;

public interface NoteRepository {

    List<Note> getByAppuserId(UUID appuserId, int pageNumber);

    Note getByIdAndAppuserId(UUID id, UUID appuserId);

    void deleteById(UUID id);

    void deleteByAppuserId(UUID appuserId);

    void save(Note note);

}
