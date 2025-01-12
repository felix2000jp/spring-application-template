package dev.felix2000jp.springapplicationtemplate.notes.application.eventhandlers;

import dev.felix2000jp.springapplicationtemplate.auth.AppuserDeletedEvent;
import dev.felix2000jp.springapplicationtemplate.notes.application.NoteService;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
class AppuserDeletedEventHandler {

    private final NoteService noteService;

    AppuserDeletedEventHandler(NoteService noteService) {
        this.noteService = noteService;
    }

    @ApplicationModuleListener
    void on(AppuserDeletedEvent event) {
        noteService.deleteNotesByAppuserId(event.appuserId());
    }

}
