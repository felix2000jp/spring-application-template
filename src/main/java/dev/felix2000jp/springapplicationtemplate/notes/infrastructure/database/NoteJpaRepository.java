package dev.felix2000jp.springapplicationtemplate.notes.infrastructure.database;

import dev.felix2000jp.springapplicationtemplate.notes.domain.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface NoteJpaRepository extends JpaRepository<Note, UUID> {

    Page<Note> findByAppuserId(UUID appuserId, Pageable pageable);

    Optional<Note> findByIdAndAppuserId(UUID id, UUID appuserId);

    void deleteAllByAppuserId(UUID appuserId);

}
