package dev.felix2000jp.springapplicationtemplate.notes.internal.dtos;

import java.util.Collection;

public record NoteListDto(Collection<NoteDto> notes) {
}
