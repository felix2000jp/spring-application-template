package dev.felix2000jp.springapplicationtemplate.notes.application.eventhandlers;

import dev.felix2000jp.springapplicationtemplate.auth.AppuserDeletedEvent;
import dev.felix2000jp.springapplicationtemplate.notes.domain.Note;
import dev.felix2000jp.springapplicationtemplate.notes.domain.NoteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.Scenario;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ApplicationModuleTest
@Testcontainers
class AppuserDeletedEventHandlerIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres");

    @Autowired
    private NoteRepository noteRepository;
    @Autowired
    private AppuserDeletedEventHandler eventHandler;

    @Test
    void givenAppuserDeletedEvent_whenOn_thenDeleteAllNotesFromDeletedAppuser(Scenario scenario) {
        // given
        var appuserId = UUID.randomUUID();
        var appuserDeletedEvent = new AppuserDeletedEvent(appuserId);

        var note1 = new Note(appuserId, "title 1", "content 1");
        var note2 = new Note(appuserId, "title 2", "content 2");
        var note3 = new Note(UUID.randomUUID(), "title 3", "content 3");

        noteRepository.save(note1);
        noteRepository.save(note2);
        noteRepository.save(note3);

        // when and then
        scenario
                .publish(appuserDeletedEvent)
                .andWaitForStateChange(() -> eventHandler)
                .andVerify(unusedEventHandler -> {
                    assertNull(noteRepository.getByIdAndAppuserId(note1.getId(), note1.getAppuserId()));
                    assertNull(noteRepository.getByIdAndAppuserId(note2.getId(), note2.getAppuserId()));
                    assertNotNull(noteRepository.getByIdAndAppuserId(note3.getId(), note3.getAppuserId()));
                });
    }

}
