package dev.felix2000jp.springapplicationtemplate.notes.application;

import dev.felix2000jp.springapplicationtemplate.shared.SecurityService;
import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.CreateNoteDto;
import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.UpdateNoteDto;
import dev.felix2000jp.springapplicationtemplate.notes.domain.Note;
import dev.felix2000jp.springapplicationtemplate.notes.domain.NoteRepository;
import dev.felix2000jp.springapplicationtemplate.notes.domain.exceptions.NoteNotFoundException;
import org.junit.jupiter.api.BeforeEach;
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
    private SecurityService securityService;
    @InjectMocks
    private NoteServiceImpl noteService;

    private SecurityService.User authenticatedUser;

    @BeforeEach
    void setUp() {
        authenticatedUser = new SecurityService.User(
                UUID.randomUUID(),
                "username",
                Set.of(SecurityService.Scope.APPLICATION.name())
        );
    }

    @Test
    void givenPage_whenGetByCurrent_User_thenReturnNotes() {
        // given
        var page = 0;
        var note = new Note(UUID.randomUUID(), "title", "content");

        when(securityService.getUser()).thenReturn(authenticatedUser);
        when(noteRepository.getByAppuserId(authenticatedUser.id(), 0)).thenReturn(List.of(note));

        // when
        var actual = noteService.getByCurrentUser(page);
        var actualNote = actual.notes().getFirst();

        // then
        assertEquals(1, actual.notes().size());
        assertEquals(note.getId(), actualNote.id());
        assertEquals(note.getTitle(), actualNote.title());
        assertEquals(note.getContent(), actualNote.content());
    }

    @Test
    void givenEmptyPage_whenGetByCurrent_User_thenReturnEmptyList() {
        // given
        var page = 0;

        when(securityService.getUser()).thenReturn(authenticatedUser);
        when(noteRepository.getByAppuserId(authenticatedUser.id(), 0)).thenReturn(List.of());

        // when
        var actual = noteService.getByCurrentUser(page);

        // then
        assertEquals(0, actual.notes().size());
    }

    @Test
    void should_get_note_with_id_from_logged_in_appuser_when_note_exists() {
        var authenticatedUser = new SecurityService.User(UUID.randomUUID(), "username", Set.of("Application"));
        var note = new Note(UUID.randomUUID(), "title", "content");

        when(securityService.getUser()).thenReturn(authenticatedUser);
        when(noteRepository.getByIdAndAppuserId(note.getId(), authenticatedUser.id())).thenReturn(note);

        var actual = noteService.getByIdAndCurrentUser(note.getId());

        assertEquals(note.getId(), actual.id());
        assertEquals(note.getTitle(), actual.title());
        assertEquals(note.getContent(), actual.content());
    }

    @Test
    void should_create_note_with_title_and_content_from_dto() {
        var authenticatedUser = new SecurityService.User(UUID.randomUUID(), "username", Set.of("Application"));
        var createNoteDTO = new CreateNoteDto("title", "content");

        when(securityService.getUser()).thenReturn(authenticatedUser);

        var actual = noteService.createByCurrentUser(createNoteDTO);

        assertEquals("title", actual.title());
        assertEquals("content", actual.content());
    }

    @Test
    void should_update_note_with_title_and_content_from_dto() {
        var authenticatedUser = new SecurityService.User(UUID.randomUUID(), "username", Set.of("Application"));
        var updateNoteDTO = new UpdateNoteDto("new title", "new content");
        var note = new Note(UUID.randomUUID(), "title", "content");

        when(securityService.getUser()).thenReturn(authenticatedUser);
        when(noteRepository.getByIdAndAppuserId(note.getId(), authenticatedUser.id())).thenReturn(note);

        var actual = noteService.updateByIdAndCurrentUser(note.getId(), updateNoteDTO);

        assertEquals("new title", actual.title());
        assertEquals("new content", actual.content());
    }

    @Test
    void should_throw_when_note_to_update_with_title_and_content_from_dto_is_not_found() {
        var authenticatedUser = new SecurityService.User(UUID.randomUUID(), "username", Set.of("Application"));
        var updateNoteDTO = new UpdateNoteDto("new title", "new content");
        var note = new Note(UUID.randomUUID(), "title", "content");

        when(securityService.getUser()).thenReturn(authenticatedUser);
        when(noteRepository.getByIdAndAppuserId(note.getId(), authenticatedUser.id())).thenReturn(null);

        var noteId = note.getId();
        assertThrows(NoteNotFoundException.class, () -> noteService.updateByIdAndCurrentUser(noteId, updateNoteDTO));
    }

    @Test
    void should_delete_note_with_id_when_note_exists() {
        var authenticatedUser = new SecurityService.User(UUID.randomUUID(), "username", Set.of("Application"));
        var note = new Note(UUID.randomUUID(), "title", "content");

        when(securityService.getUser()).thenReturn(authenticatedUser);
        when(noteRepository.getByIdAndAppuserId(note.getId(), authenticatedUser.id())).thenReturn(note);

        var actual = noteService.deleteByIdAndCurrentUser(note.getId());

        assertEquals(note.getId(), actual.id());
        assertEquals(note.getTitle(), actual.title());
        assertEquals(note.getContent(), actual.content());
    }

    @Test
    void should_throw_when_deleting_note_with_id_is_not_found() {
        var authenticatedUser = new SecurityService.User(UUID.randomUUID(), "username", Set.of("Application"));
        var note = new Note(UUID.randomUUID(), "title", "content");

        when(securityService.getUser()).thenReturn(authenticatedUser);
        when(noteRepository.getByIdAndAppuserId(note.getId(), authenticatedUser.id())).thenReturn(null);

        var noteId = note.getId();
        assertThrows(NoteNotFoundException.class, () -> noteService.deleteByIdAndCurrentUser(noteId));
    }

}
