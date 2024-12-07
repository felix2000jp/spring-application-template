package dev.felix2000jp.springapplicationtemplate.notes.application.dtos;

import jakarta.validation.constraints.NotBlank;

public record CreateNoteDTO(@NotBlank String title, @NotBlank String content) {
}
