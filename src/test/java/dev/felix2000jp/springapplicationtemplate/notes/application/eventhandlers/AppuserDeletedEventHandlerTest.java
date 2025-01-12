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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AppuserDeletedEventHandlerTest {

    @Mock
    private NoteService noteService;
    @InjectMocks
    private AppuserDeletedEventHandler eventHandler;

    @Test
    void givenAppuserDeletedEvent_whenOn_thenDeleteAllNotesFromDeletedAppuser() {
        // given
        var appuserId = UUID.randomUUID();
        var appuserDeletedEvent = new AppuserDeletedEvent(appuserId);

        // when
        eventHandler.on(appuserDeletedEvent);

        // then
        var uuidCaptor = ArgumentCaptor.forClass(UUID.class);
        verify(noteService).deleteByAppuserId(uuidCaptor.capture());
        assertEquals(appuserId, uuidCaptor.getValue());
    }

}
