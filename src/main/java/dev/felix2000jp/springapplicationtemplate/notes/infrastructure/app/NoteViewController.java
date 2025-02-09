package dev.felix2000jp.springapplicationtemplate.notes.infrastructure.app;

import dev.felix2000jp.springapplicationtemplate.notes.application.NoteService;
import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.NoteListDto;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Validated
@Controller
@RequestMapping("/app/notes")
class NoteViewController {

    private final NoteService noteService;

    NoteViewController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping
    ResponseEntity<NoteListDto> getNotesForCurrentUser(@RequestParam(defaultValue = "0") @Min(0) int page) {
        var body = noteService.getNotesForCurrentUser(page);
        return ResponseEntity.ok(body);
    }

}
