package dev.felix2000jp.springapplicationtemplate.notes.api;

import dev.felix2000jp.springapplicationtemplate.notes.application.NoteService;
import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.CreateNoteDTO;
import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.NoteDTO;
import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.NoteListDTO;
import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.UpdateNoteDTO;
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

    @GetMapping
    ResponseEntity<NoteListDTO> getByPage(@RequestParam @Min(0) int page) {
        var body = noteService.getByAppuser(page);
        return ResponseEntity.ok(body);
    }

    @GetMapping("/{id}")
    ResponseEntity<NoteDTO> getById(@PathVariable UUID id) {
        var body = noteService.getByIdAndAppuser(id);
        return ResponseEntity.ok(body);
    }

    @PostMapping
    ResponseEntity<NoteDTO> create(@RequestBody @Valid CreateNoteDTO createNoteDTO) {
        var body = noteService.createByAppuser(createNoteDTO);
        var location = URI.create("/api/notes/" + body.id());
        return ResponseEntity.created(location).body(body);
    }

    @PutMapping("/{id}")
    ResponseEntity<Void> update(@PathVariable UUID id, @RequestBody @Valid UpdateNoteDTO updateNoteDTO) {
        noteService.updateByIdAndAppuser(id, updateNoteDTO);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable UUID id) {
        noteService.deleteByIdAndAppuser(id);
        return ResponseEntity.noContent().build();
    }

}
