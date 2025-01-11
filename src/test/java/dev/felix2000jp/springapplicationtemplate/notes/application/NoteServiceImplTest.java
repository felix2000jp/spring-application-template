package dev.felix2000jp.springapplicationtemplate.notes.application;

import dev.felix2000jp.springapplicationtemplate.auth.AuthClient;
import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.CreateNoteDTO;
import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.UpdateNoteDTO;
import dev.felix2000jp.springapplicationtemplate.notes.domain.Note;
import dev.felix2000jp.springapplicationtemplate.notes.domain.NoteRepository;
import dev.felix2000jp.springapplicationtemplate.notes.domain.exceptions.NoteNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NoteServiceImplTest {

    @Mock
    private NoteRepository noteRepository;
    @Spy
    private NoteMapper noteMapper;
    @Mock
    private AuthClient authClient;
    @InjectMocks
    private NoteServiceImpl noteService;

    @Test
    void should_get_notes_from_logged_in_appuser_when_notes_exist() {
        var authenticatedUser = new AuthClient.User(UUID.randomUUID(), "username", Set.of("Application"));
        var note = new Note(UUID.randomUUID(), "title", "content");

        when(authClient.getUser()).thenReturn(authenticatedUser);
        when(noteRepository.getByAppuserId(authenticatedUser.id(), 0)).thenReturn(List.of(note));

        var actual = noteService.getByAppuser(0);
        var actualNote = actual.notes().getFirst();

        assertEquals(1, actual.notes().size());
        assertEquals(note.getId(), actualNote.id());
        assertEquals(note.getTitle(), actualNote.title());
        assertEquals(note.getContent(), actualNote.content());
    }

    @Test
    void should_not_get_notes_from_logged_in_appuser_when_notes_do_not_exist() {
        var authenticatedUser = new AuthClient.User(UUID.randomUUID(), "username", Set.of("Application"));

        when(authClient.getUser()).thenReturn(authenticatedUser);
        when(noteRepository.getByAppuserId(authenticatedUser.id(), 0)).thenReturn(List.of());

        var actual = noteService.getByAppuser(0);

        assertEquals(0, actual.notes().size());
    }

    @Test
    void should_get_note_with_id_from_logged_in_appuser_when_note_exists() {
        var authenticatedUser = new AuthClient.User(UUID.randomUUID(), "username", Set.of("Application"));
        var note = new Note(UUID.randomUUID(), "title", "content");

        when(authClient.getUser()).thenReturn(authenticatedUser);
        when(noteRepository.getByIdAndAppuserId(note.getId(), authenticatedUser.id())).thenReturn(note);

        var actual = noteService.getByIdAndAppuser(note.getId());

        assertEquals(note.getId(), actual.id());
        assertEquals(note.getTitle(), actual.title());
        assertEquals(note.getContent(), actual.content());
    }

    @Test
    void should_not_get_note_with_id_from_logged_in_appuser_when_note_does_not_exist() {
        var authenticatedUser = new AuthClient.User(UUID.randomUUID(), "username", Set.of("Application"));
        var note = new Note(UUID.randomUUID(), "title", "content");

        when(authClient.getUser()).thenReturn(authenticatedUser);
        when(noteRepository.getByIdAndAppuserId(note.getId(), authenticatedUser.id())).thenReturn(null);

        var noteId = note.getId();
        assertThrows(NoteNotFoundException.class, () -> noteService.getByIdAndAppuser(noteId));
    }

    @Test
    void should_create_note_with_title_and_content_from_dto() {
        var authenticatedUser = new AuthClient.User(UUID.randomUUID(), "username", Set.of("Application"));
        var createNoteDTO = new CreateNoteDTO("title", "content");

        when(authClient.getUser()).thenReturn(authenticatedUser);

        var actual = noteService.createByAppuser(createNoteDTO);

        assertEquals("title", actual.title());
        assertEquals("content", actual.content());
    }

    @Test
    void should_update_note_with_title_and_content_from_dto() {
        var authenticatedUser = new AuthClient.User(UUID.randomUUID(), "username", Set.of("Application"));
        var updateNoteDTO = new UpdateNoteDTO("new title", "new content");
        var note = new Note(UUID.randomUUID(), "title", "content");

        when(authClient.getUser()).thenReturn(authenticatedUser);
        when(noteRepository.getByIdAndAppuserId(note.getId(), authenticatedUser.id())).thenReturn(note);

        var actual = noteService.updateByIdAndAppuser(note.getId(), updateNoteDTO);

        assertEquals("new title", actual.title());
        assertEquals("new content", actual.content());
    }

    @Test
    void should_throw_when_note_to_update_with_title_and_content_from_dto_is_not_found() {
        var authenticatedUser = new AuthClient.User(UUID.randomUUID(), "username", Set.of("Application"));
        var updateNoteDTO = new UpdateNoteDTO("new title", "new content");
        var note = new Note(UUID.randomUUID(), "title", "content");

        when(authClient.getUser()).thenReturn(authenticatedUser);
        when(noteRepository.getByIdAndAppuserId(note.getId(), authenticatedUser.id())).thenReturn(null);

        var noteId = note.getId();
        assertThrows(NoteNotFoundException.class, () -> noteService.updateByIdAndAppuser(noteId, updateNoteDTO));
    }

    @Test
    void should_delete_note_with_id_when_note_exists() {
        var authenticatedUser = new AuthClient.User(UUID.randomUUID(), "username", Set.of("Application"));
        var note = new Note(UUID.randomUUID(), "title", "content");

        when(authClient.getUser()).thenReturn(authenticatedUser);
        when(noteRepository.getByIdAndAppuserId(note.getId(), authenticatedUser.id())).thenReturn(note);

        var actual = noteService.deleteByIdAndAppuser(note.getId());

        assertEquals(note.getId(), actual.id());
        assertEquals(note.getTitle(), actual.title());
        assertEquals(note.getContent(), actual.content());
    }

    @Test
    void should_throw_when_deleting_note_with_id_is_not_found() {
        var authenticatedUser = new AuthClient.User(UUID.randomUUID(), "username", Set.of("Application"));
        var note = new Note(UUID.randomUUID(), "title", "content");

        when(authClient.getUser()).thenReturn(authenticatedUser);
        when(noteRepository.getByIdAndAppuserId(note.getId(), authenticatedUser.id())).thenReturn(null);

        var noteId = note.getId();
        assertThrows(NoteNotFoundException.class, () -> noteService.deleteByIdAndAppuser(noteId));
    }

}
