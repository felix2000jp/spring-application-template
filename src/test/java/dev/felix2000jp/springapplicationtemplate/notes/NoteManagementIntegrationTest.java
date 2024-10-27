package dev.felix2000jp.springapplicationtemplate.notes;

import dev.felix2000jp.springapplicationtemplate.appusers.AppuserDeletedEvent;
import dev.felix2000jp.springapplicationtemplate.notes.internal.Note;
import dev.felix2000jp.springapplicationtemplate.notes.internal.NoteRepository;
import dev.felix2000jp.springapplicationtemplate.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.Scenario;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@ApplicationModuleTest(extraIncludes = {"shared", "appusers"}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class NoteManagementIntegrationTest {

    @Autowired
    private NoteRepository noteRepository;

    @Test
    void on_AppuserDeletedEvent_should_delete_all_notes_with_appuserId(Scenario scenario) {
        var appuserId = UUID.randomUUID();

        var note1 = new Note("title 1", "content", appuserId);
        var note2 = new Note("title 2", "content", appuserId);
        var note3 = new Note("title 3", "content", appuserId);
        var note4 = new Note("title 4", "content", appuserId);
        var note5 = new Note("title 5", "content", appuserId);

        noteRepository.saveAll(List.of(note1, note2, note3, note4, note5));

        var event = new AppuserDeletedEvent(appuserId);
        scenario.publish(event)
                .andWaitForEventOfType(AppuserDeletedEvent.class)
                .toArriveAndVerify(e -> assertThat(e.appuserId()).isEqualTo(appuserId));

        var actual = noteRepository.findAll();
        assertThat(actual).isEmpty();
    }

}
