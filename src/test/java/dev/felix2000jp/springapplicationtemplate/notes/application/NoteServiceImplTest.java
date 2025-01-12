package dev.felix2000jp.springapplicationtemplate.notes.application;

import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.CreateNoteDto;
import dev.felix2000jp.springapplicationtemplate.notes.application.dtos.UpdateNoteDto;
import dev.felix2000jp.springapplicationtemplate.notes.domain.Note;
import dev.felix2000jp.springapplicationtemplate.notes.domain.NoteRepository;
import dev.felix2000jp.springapplicationtemplate.notes.domain.exceptions.NoteNotFoundException;
import dev.felix2000jp.springapplicationtemplate.shared.SecurityService;
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

        when(securityService.getUser()).thenReturn(authenticatedUser);
    }

    @Test
    void givenPage_whenGetByCurrentUser_User_thenReturnNotes() {
        // given
        var page = 0;
        var note = new Note(UUID.randomUUID(), "title", "content");

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
    void givenEmptyPage_whenGetByCurrentUser_User_thenReturnEmptyList() {
        // given
        var page = 0;

        when(noteRepository.getByAppuserId(authenticatedUser.id(), 0)).thenReturn(List.of());

        // when
        var actual = noteService.getByCurrentUser(page);

        // then
        assertEquals(0, actual.notes().size());
    }

    @Test
    void givenId_getByIdAndCurrentUser_thenReturnNoteDto() {
        // given
        var note = new Note(UUID.randomUUID(), "title", "content");
        var id = note.getId();

        when(noteRepository.getByIdAndAppuserId(id, authenticatedUser.id())).thenReturn(note);

        // when
        var actual = noteService.getByIdAndCurrentUser(id);

        // then
        assertEquals(note.getId(), actual.id());
        assertEquals(note.getTitle(), actual.title());
        assertEquals(note.getContent(), actual.content());
    }

    @Test
    void givenNonExistentId_getByIdAndCurrentUser_thenThrowNoteNotFoundException() {
        // given
        var note = new Note(UUID.randomUUID(), "title", "content");
        var id = note.getId();

        when(noteRepository.getByIdAndAppuserId(id, authenticatedUser.id())).thenReturn(null);

        // when and then
        assertThrows(NoteNotFoundException.class, () -> noteService.getByIdAndCurrentUser(id));
    }

    @Test
    void givenCreateNoteDto_createByCurrentUser_thenCreateNote() {
        // given
        var createNoteDto = new CreateNoteDto("title", "content");

        // when
        var actual = noteService.createByCurrentUser(createNoteDto);

        // then
        assertEquals(createNoteDto.title(), actual.title());
        assertEquals(createNoteDto.content(), actual.content());
    }

    @Test
    void givenIdAndUpdateNoteDto_whenUpdateByIdAndCurrentUser_thenUpdateNote() {
        // given
        var note = new Note(UUID.randomUUID(), "title", "content");
        var id = note.getId();
        var updateNoteDto = new UpdateNoteDto("new title", "new content");

        when(noteRepository.getByIdAndAppuserId(id, authenticatedUser.id())).thenReturn(note);

        // when
        var actual = noteService.updateByIdAndCurrentUser(id, updateNoteDto);

        // then
        assertEquals(updateNoteDto.title(), actual.title());
        assertEquals(updateNoteDto.content(), actual.content());
    }

    @Test
    void givenNonExistentIdAndUpdateNoteDto_whenUpdateByIdAndCurrentUser_thenThrowNoteNotFoundException() {
        // given
        var id = UUID.randomUUID();
        var updateNoteDTO = new UpdateNoteDto("new title", "new content");

        when(noteRepository.getByIdAndAppuserId(id, authenticatedUser.id())).thenReturn(null);

        // when and then
        assertThrows(NoteNotFoundException.class, () -> noteService.updateByIdAndCurrentUser(id, updateNoteDTO));
    }

    @Test
    void givenId_whenDeleteByIdAndCurrentUser_thenDeleteNote() {
        // given
        var note = new Note(UUID.randomUUID(), "title", "content");
        var id = note.getId();

        when(noteRepository.getByIdAndAppuserId(id, authenticatedUser.id())).thenReturn(note);

        // when
        var actual = noteService.deleteByIdAndCurrentUser(id);

        // then
        assertEquals(note.getId(), actual.id());
        assertEquals(note.getTitle(), actual.title());
        assertEquals(note.getContent(), actual.content());
    }

    @Test
    void givenNonExistentId_whenDeleteByIdAndCurrentUser_thenThrowNoteNotFoundException() {
        // given
        var id = UUID.randomUUID();

        when(noteRepository.getByIdAndAppuserId(id, authenticatedUser.id())).thenReturn(null);

        // when and then
        assertThrows(NoteNotFoundException.class, () -> noteService.deleteByIdAndCurrentUser(id));
    }

}
