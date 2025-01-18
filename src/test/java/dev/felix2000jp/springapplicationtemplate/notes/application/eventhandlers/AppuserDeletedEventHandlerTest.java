package dev.felix2000jp.springapplicationtemplate.notes.application.eventhandlers;

import dev.felix2000jp.springapplicationtemplate.auth.AppuserDeletedEvent;
import dev.felix2000jp.springapplicationtemplate.notes.application.NoteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AppuserDeletedEventHandlerTest {

    @Mock
    private NoteService noteService;
    @InjectMocks
    private AppuserDeletedEventHandler eventHandler;

    @Test
    void on_given_event_then_delete_all_notes_with_appuser_id() {
        var appuserId = UUID.randomUUID();
        var appuserDeletedEvent = new AppuserDeletedEvent(appuserId);

        eventHandler.on(appuserDeletedEvent);

        var uuidCaptor = ArgumentCaptor.forClass(UUID.class);
        verify(noteService).deleteNotesByAppuserId(uuidCaptor.capture());
        assertThat(uuidCaptor.getValue()).isEqualTo(appuserId);
    }

}
