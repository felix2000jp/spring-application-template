package dev.felix2000jp.springapplicationtemplate.notes.application.dtos;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record NoteListDto(@NotNull List<NoteDto> notes) {
}
