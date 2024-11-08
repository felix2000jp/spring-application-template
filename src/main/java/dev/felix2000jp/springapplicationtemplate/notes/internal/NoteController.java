package dev.felix2000jp.springapplicationtemplate.notes.internal;

import dev.felix2000jp.springapplicationtemplate.notes.internal.dtos.CreateNoteDto;
import dev.felix2000jp.springapplicationtemplate.notes.internal.dtos.NoteDto;
import dev.felix2000jp.springapplicationtemplate.notes.internal.dtos.NoteListDto;
import dev.felix2000jp.springapplicationtemplate.notes.internal.dtos.UpdateNoteDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/notes")
class NoteController {

    private final NoteService noteService;

    NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping
    ResponseEntity<NoteListDto> findAll() {
        var body = noteService.findAll();
        return ResponseEntity.ok(body);
    }

    @GetMapping("/{id}")
    ResponseEntity<NoteDto> findById(@PathVariable UUID id) {
        var body = noteService.find(id);
        return ResponseEntity.ok(body);
    }

    @PostMapping
    ResponseEntity<NoteDto> create(@Valid @RequestBody CreateNoteDto createNoteDto) {
        var body = noteService.create(createNoteDto);
        var location = URI.create("/api/notes/" + body.id());
        return ResponseEntity.created(location).body(body);
    }

    @PutMapping("/{id}")
    ResponseEntity<Void> update(@PathVariable UUID id, @Valid @RequestBody UpdateNoteDto updateNoteDto) {
        noteService.update(id, updateNoteDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable UUID id) {
        noteService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
