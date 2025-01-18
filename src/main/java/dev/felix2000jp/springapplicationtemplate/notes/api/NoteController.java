package dev.felix2000jp.springapplicationtemplate.notes.api;

import dev.felix2000jp.springapplicationtemplate.notes.application.NoteService;
import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.CreateNoteDto;
import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.NoteDto;
import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.NoteListDto;
import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.UpdateNoteDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/api/notes")
class NoteController {

    private final NoteService noteService;

    NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @PostMapping
    ResponseEntity<NoteDto> createNoteForCurrentUser(@RequestBody @Valid CreateNoteDto createNoteDto) {
        var body = noteService.createNoteForCurrentUser(createNoteDto);
        var location = URI.create("/api/notes/" + body.id());
        return ResponseEntity.created(location).body(body);
    }

    @GetMapping
    ResponseEntity<NoteListDto> getNotesForCurrentUser(@RequestParam(defaultValue = "0") @Min(0) int page) {
        var body = noteService.getNotesForCurrentUser(page);
        return ResponseEntity.ok(body);
    }

    @GetMapping("/{id}")
    ResponseEntity<NoteDto> getNoteByIdForCurrentUser(@PathVariable UUID id) {
        var body = noteService.getNoteByIdForCurrentUser(id);
        return ResponseEntity.ok(body);
    }

    @PutMapping("/{id}")
    ResponseEntity<Void> updateNoteByIdForCurrentUser(@PathVariable UUID id, @RequestBody @Valid UpdateNoteDto updateNoteDto) {
        noteService.updateNoteByIdForCurrentUser(id, updateNoteDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteNoteByIdForCurrentUser(@PathVariable UUID id) {
        noteService.deleteNoteByIdForCurrentUser(id);
        return ResponseEntity.noContent().build();
    }

}
