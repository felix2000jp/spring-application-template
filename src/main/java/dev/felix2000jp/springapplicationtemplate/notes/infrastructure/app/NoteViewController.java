package dev.felix2000jp.springapplicationtemplate.notes.infrastructure.app;

import dev.felix2000jp.springapplicationtemplate.notes.application.NoteService;
import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.CreateNoteDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Validated
@Controller
@RequestMapping("/app")
class NoteViewController {

    private final NoteService noteService;

    NoteViewController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping
    String getNotesForCurrentUser(@RequestParam(defaultValue = "0") @Min(0) int page, Model model) {
        var body = noteService.getNotesForCurrentUser(page);
        model.addAttribute("notes", body.notes());

        return "index";
    }

    @PostMapping("/notes")
    public String addNote(@Valid @ModelAttribute CreateNoteDto createNoteDto, Model model) {
        var newNote = noteService.createNoteForCurrentUser(createNoteDto);
        model.addAttribute("note", newNote);

        return "notes/note-card";
    }

    @DeleteMapping("/notes")
    public String deleteNote(@RequestParam UUID id, Model model) {
        var deletedNote = noteService.deleteNoteByIdForCurrentUser(id);
        model.addAttribute("note", deletedNote);

        return "notes/note-card-deleted";
    }

}
