package dev.felix2000jp.springapplicationtemplate.notes.application.eventhandlers;

import dev.felix2000jp.springapplicationtemplate.appusers.AppuserDeletedEvent;
import dev.felix2000jp.springapplicationtemplate.notes.application.NoteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AppuserDeletedEventHandlerTest {

    @Mock
    private NoteService noteService;
    @InjectMocks
    private AppuserDeletedEventHandler eventHandler;

    @Test
    void should_perform_delete_on_appuser_when_event_is_called_with_appuserId() {
        var appuserId = UUID.randomUUID();
        var event = new AppuserDeletedEvent(appuserId);

        eventHandler.on(event);

        var uuidCaptor = ArgumentCaptor.forClass(UUID.class);
        verify(noteService).deleteByAppuserId(uuidCaptor.capture());
        assertEquals(appuserId, uuidCaptor.getValue());
    }
}
