package dev.felix2000jp.springapplicationtemplate.notes;

import dev.felix2000jp.springapplicationtemplate.appusers.AppuserDeletedEvent;
import dev.felix2000jp.springapplicationtemplate.notes.internal.NoteService;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
public class NoteManagement {

    private final NoteService noteService;

    public NoteManagement(NoteService noteService) {
        this.noteService = noteService;
    }

    @ApplicationModuleListener
    void on(AppuserDeletedEvent event) {
        noteService.deleteAllByAppuserId(event.appuserId());
    }

}
