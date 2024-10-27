package dev.felix2000jp.springapplicationtemplate.notes.internal;

import dev.felix2000jp.springapplicationtemplate.notes.internal.dtos.CreateNoteDto;
import dev.felix2000jp.springapplicationtemplate.notes.internal.dtos.NoteDto;
import dev.felix2000jp.springapplicationtemplate.notes.internal.dtos.NoteListDto;
import dev.felix2000jp.springapplicationtemplate.notes.internal.dtos.UpdateNoteDto;
import dev.felix2000jp.springapplicationtemplate.shared.AppuserPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping
    ResponseEntity<NoteListDto> findAll(Authentication authentication) {
        var principal = (AppuserPrincipal) authentication.getPrincipal();
        var body = noteService.findAll(principal);
        return ResponseEntity.ok(body);
    }

    @GetMapping("/{id}")
    ResponseEntity<NoteDto> findById(Authentication authentication, @PathVariable UUID id) {
        var principal = (AppuserPrincipal) authentication.getPrincipal();
        var body = noteService.findById(principal, id);
        return ResponseEntity.ok(body);
    }

    @PostMapping
    ResponseEntity<NoteDto> create(Authentication authentication, @Valid @RequestBody CreateNoteDto createNoteDto) {
        var principal = (AppuserPrincipal) authentication.getPrincipal();
        var body = noteService.create(principal, createNoteDto);
        var location = URI.create("/api/notes/" + body.id());
        return ResponseEntity.created(location).body(body);
    }

    @PutMapping("/{id}")
    ResponseEntity<Void> update(Authentication authentication, @PathVariable UUID id, @Valid @RequestBody UpdateNoteDto updateNoteDto) {
        var principal = (AppuserPrincipal) authentication.getPrincipal();
        noteService.update(principal, id, updateNoteDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(Authentication authentication, @PathVariable UUID id) {
        var principal = (AppuserPrincipal) authentication.getPrincipal();
        noteService.delete(principal, id);
        return ResponseEntity.noContent().build();
    }

}
