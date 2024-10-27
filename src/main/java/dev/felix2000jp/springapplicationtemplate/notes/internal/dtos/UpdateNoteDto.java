package dev.felix2000jp.springapplicationtemplate.notes.internal.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateNoteDto(
        @Size(min = 3, max = 150)
        @NotBlank
        String title,
        @Size(min = 3, max = 5000)
        @NotBlank
        String content
) {
}
