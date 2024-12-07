package dev.felix2000jp.springapplicationtemplate.notes.application.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record NoteDTO(@NotNull UUID id, @NotBlank String title, @NotBlank String content) {
}
