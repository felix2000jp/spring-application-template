package dev.felix2000jp.springapplicationtemplate.notes.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NoteTest {

    @Test
    void should_create_new_note_successfully() {
        var appuserId = UUID.randomUUID();

        var actual = new Note(appuserId, "title", "content");

        assertEquals(appuserId, actual.getAppuserId());
        assertEquals("title", actual.getTitle());
        assertEquals("content", actual.getContent());
    }

    @Test
    void should_set_title_successfully() {
        var note = new Note(UUID.randomUUID(), "title", "content");

        note.setTitle("new title");

        assertEquals("new title", note.getTitle());
    }

    @Test
    void should_set_content_successfully() {
        var note = new Note(UUID.randomUUID(), "title", "content");

        note.setContent("new content");

        assertEquals("new content", note.getContent());
    }

}
