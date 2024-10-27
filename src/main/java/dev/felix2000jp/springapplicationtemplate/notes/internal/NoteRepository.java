package dev.felix2000jp.springapplicationtemplate.notes.internal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NoteRepository extends JpaRepository<Note, UUID> {

    Collection<Note> findByAppuserId(UUID appuserId);

    Optional<Note> findByIdAndAppuserId(UUID id, UUID appuserId);

    void deleteAllByAppuserId(UUID appuserId);
}
