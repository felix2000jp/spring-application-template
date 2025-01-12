package dev.felix2000jp.springapplicationtemplate.notes.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NoteTest {

    @Test
    void givenNoteFieldValues_whenNewNote_thenNoteIsCreated() {
        // given
        var appuserId = UUID.randomUUID();
        var title = "title";
        var content = "content";

        // when
        var actual = new Note(appuserId, title, content);

        // then
        assertEquals(appuserId, actual.getAppuserId());
        assertEquals(title, actual.getTitle());
        assertEquals(content, actual.getContent());
    }

    @Test
    void givenNoteAndNewTitle_whenSetTitle_thenTitleIsUpdated() {
        // given
        var note = new Note(UUID.randomUUID(), "title", "content");
        var newTitle = "new title";

        // when
        note.setTitle(newTitle);

        // then
        assertEquals(newTitle, note.getTitle());
    }

    @Test
    void givenNoteAndNewContent_whenSetContent_thenContentIsUpdated() {
        // given
        var note = new Note(UUID.randomUUID(), "title", "content");
        var newContent = "new content";

        // when
        note.setContent(newContent);

        // then
        assertEquals(newContent, note.getContent());
    }

}
