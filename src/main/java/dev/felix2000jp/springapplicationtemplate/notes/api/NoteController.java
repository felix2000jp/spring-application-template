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

    @GetMapping
    ResponseEntity<NoteListDto> getByPage(@RequestParam(defaultValue = "0") @Min(0) int page) {
        var body = noteService.getByAppuser(page);
        return ResponseEntity.ok(body);
    }

    @GetMapping("/{id}")
    ResponseEntity<NoteDto> getById(@PathVariable UUID id) {
        var body = noteService.getByIdAndAppuser(id);
        return ResponseEntity.ok(body);
    }

    @PostMapping
    ResponseEntity<NoteDto> create(@RequestBody @Valid CreateNoteDto createNoteDTO) {
        var body = noteService.createByAppuser(createNoteDTO);
        var location = URI.create("/api/notes/" + body.id());
        return ResponseEntity.created(location).body(body);
    }

    @PutMapping("/{id}")
    ResponseEntity<Void> update(@PathVariable UUID id, @RequestBody @Valid UpdateNoteDto updateNoteDTO) {
        noteService.updateByIdAndAppuser(id, updateNoteDTO);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable UUID id) {
        noteService.deleteByIdAndAppuser(id);
        return ResponseEntity.noContent().build();
    }

}
