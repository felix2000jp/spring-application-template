package dev.felix2000jp.springapplicationtemplate.notes.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class NoteTest {

    @Test
    void constructor_given_valid_parameters_then_create_note() {
        var appuserId = UUID.randomUUID();
        var actual = new Note(appuserId, "title", "content");

        assertThat(actual.getAppuserId()).isEqualTo(appuserId);
        assertThat(actual.getTitle()).isEqualTo("title");
        assertThat(actual.getContent()).isEqualTo("content");
    }

    @Test
    void setTitle_given_new_title_then_update_title() {
        var note = new Note(UUID.randomUUID(), "title", "content");

        note.setTitle("new title");

        assertThat(note.getTitle()).isEqualTo("new title");
    }

    @Test
    void setContent_given_new_content_then_update_content() {
        var note = new Note(UUID.randomUUID(), "title", "content");

        note.setContent("new content");

        assertThat(note.getContent()).isEqualTo("new content");
    }

}
