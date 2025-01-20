package dev.felix2000jp.springapplicationtemplate.notes.infrastructure.queue;

import dev.felix2000jp.springapplicationtemplate.auth.application.events.AppuserDeletedEvent;
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

import static org.assertj.core.api.Assertions.assertThat;

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
    void on_given_event_then_delete_all_notes_with_appuser_id(Scenario scenario) {
        var appuserId = UUID.randomUUID();
        var appuserDeletedEvent = new AppuserDeletedEvent(appuserId);

        var note1 = new Note(appuserId, "title 1", "content 1");
        var note2 = new Note(appuserId, "title 2", "content 2");
        var note3 = new Note(UUID.randomUUID(), "title 3", "content 3");

        noteRepository.save(note1);
        noteRepository.save(note2);
        noteRepository.save(note3);

        scenario
                .publish(appuserDeletedEvent)
                .andWaitForStateChange(() -> eventHandler)
                .andVerify(unusedEventHandler -> {
                    assertThat(noteRepository.findByIdAndAppuserId(note1.getId(), note1.getAppuserId())).isNotPresent();
                    assertThat(noteRepository.findByIdAndAppuserId(note2.getId(), note2.getAppuserId())).isNotPresent();
                    assertThat(noteRepository.findByIdAndAppuserId(note3.getId(), note3.getAppuserId())).isPresent();
                });
    }

}
