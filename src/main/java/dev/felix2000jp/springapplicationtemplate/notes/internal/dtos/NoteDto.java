package dev.felix2000jp.springapplicationtemplate.notes.internal.dtos;

import java.util.UUID;

public record NoteDto(UUID id, String title, String content) {
}
