package dev.felix2000jp.springapplicationtemplate.notes.api;

import dev.felix2000jp.springapplicationtemplate.notes.application.NoteService;
import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.NoteListDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notes")
class NoteController {

    private final NoteService noteService;

    NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping
    ResponseEntity<NoteListDTO> getNotes(@RequestParam int page) {
        var body = noteService.getByAppuser(page);
        return ResponseEntity.ok(body);
    }

}
