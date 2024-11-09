package dev.felix2000jp.springapplicationtemplate.notes.internal;

import dev.felix2000jp.springapplicationtemplate.notes.internal.dtos.CreateNoteDTO;
import dev.felix2000jp.springapplicationtemplate.notes.internal.dtos.NoteDTO;
import dev.felix2000jp.springapplicationtemplate.notes.internal.dtos.NoteListDTO;
import dev.felix2000jp.springapplicationtemplate.notes.internal.dtos.UpdateNoteDTO;
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
    ResponseEntity<NoteListDTO> findAll() {
        var body = noteService.findAll();
        return ResponseEntity.ok(body);
    }

    @GetMapping("/{id}")
    ResponseEntity<NoteDTO> findById(@PathVariable UUID id) {
        var body = noteService.find(id);
        return ResponseEntity.ok(body);
    }

    @PostMapping
    ResponseEntity<NoteDTO> create(@Valid @RequestBody CreateNoteDTO createNoteDTO) {
        var body = noteService.create(createNoteDTO);
        var location = URI.create("/api/notes/" + body.id());
        return ResponseEntity.created(location).body(body);
    }

    @PutMapping("/{id}")
    ResponseEntity<Void> update(@PathVariable UUID id, @Valid @RequestBody UpdateNoteDTO updateNoteDTO) {
        noteService.update(id, updateNoteDTO);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable UUID id) {
        noteService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
