package dev.felix2000jp.springapplicationtemplate.notes.internal.dtos;

import java.util.UUID;

public record NoteDTO(UUID id, String title, String content) {
}
