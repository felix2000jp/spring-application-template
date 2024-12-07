package dev.felix2000jp.springapplicationtemplate.notes.application.dtos;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record NoteListDTO(@NotNull List<NoteDTO> notes) {
}
