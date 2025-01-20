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
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NoteServiceTest {

    @Mock
    private NoteRepository noteRepository;
    @Spy
    private NoteMapper noteMapper;
    @Mock
    private SecurityService securityService;
    @InjectMocks
    private NoteService noteService;

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
    void getNotesForCurrentUser_given_page_then_return_list_of_notes() {
        var note = new Note(UUID.randomUUID(), "title", "content");

        when(noteRepository.findAllByAppuserId(authenticatedUser.id(), 0)).thenReturn(List.of(note));

        var actual = noteService.getNotesForCurrentUser(0);
        var actualNote = actual.notes().getFirst();

        assertThat(actual.notes()).hasSize(1);
        assertThat(actualNote.id()).isEqualTo(note.getId());
        assertThat(actualNote.title()).isEqualTo(note.getTitle());
        assertThat(actualNote.content()).isEqualTo(note.getContent());
    }

    @Test
    void getNotesForCurrentUser_given_empty_page_then_return_empty_list_of_notes() {
        when(noteRepository.findAllByAppuserId(authenticatedUser.id(), 0)).thenReturn(List.of());

        var actual = noteService.getNotesForCurrentUser(0);

        assertThat(actual.notes()).isEmpty();
    }

    @Test
    void getNoteByIdForCurrentUser_given_note_id_then_return_note() {
        var note = new Note(UUID.randomUUID(), "title", "content");

        when(noteRepository.findByIdAndAppuserId(note.getId(), authenticatedUser.id())).thenReturn(Optional.of(note));

        var actual = noteService.getNoteByIdForCurrentUser(note.getId());

        assertThat(actual.id()).isEqualTo(note.getId());
        assertThat(actual.title()).isEqualTo(note.getTitle());
        assertThat(actual.content()).isEqualTo(note.getContent());
    }

    @Test
    void findByIdAndAppuserId_given_not_found_id_then_throw_note_not_found_exception() {
        var id = UUID.randomUUID();

        when(noteRepository.findByIdAndAppuserId(id, authenticatedUser.id())).thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> noteService.getNoteByIdForCurrentUser(id)
        ).isInstanceOf(NoteNotFoundException.class);
    }

    @Test
    void createNoteForCurrentUser_given_dto_then_create_note() {
        var createNoteDto = new CreateNoteDto("title", "content");

        var actual = noteService.createNoteForCurrentUser(createNoteDto);

        assertThat(actual.title()).isEqualTo(createNoteDto.title());
        assertThat(actual.content()).isEqualTo(createNoteDto.content());
    }

    @Test
    void updateNoteByIdForCurrentUser_given_id_and_dto_then_update_note() {
        var note = new Note(UUID.randomUUID(), "title", "content");
        var updateNoteDto = new UpdateNoteDto("new title", "new content");

        when(noteRepository.findByIdAndAppuserId(note.getId(), authenticatedUser.id())).thenReturn(Optional.of(note));

        var actual = noteService.updateNoteByIdForCurrentUser(note.getId(), updateNoteDto);

        assertThat(actual.title()).isEqualTo(updateNoteDto.title());
        assertThat(actual.content()).isEqualTo(updateNoteDto.content());
    }

    @Test
    void UpdateNoteByIdForCurrentUser_given_not_found_id_and_dto_then_throw_note_not_found_exception() {
        var id = UUID.randomUUID();
        var updateNoteDto = new UpdateNoteDto("new title", "new content");

        when(noteRepository.findByIdAndAppuserId(id, authenticatedUser.id())).thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> noteService.updateNoteByIdForCurrentUser(id, updateNoteDto)
        ).isInstanceOf(NoteNotFoundException.class);
    }

    @Test
    void deleteNoteByIdForCurrentUser_given_note_id_then_delete_note() {
        var note = new Note(UUID.randomUUID(), "title", "content");

        when(noteRepository.findByIdAndAppuserId(note.getId(), authenticatedUser.id())).thenReturn(Optional.of(note));

        var actual = noteService.deleteNoteByIdForCurrentUser(note.getId());

        assertThat(actual.id()).isEqualTo(note.getId());
        assertThat(actual.title()).isEqualTo(note.getTitle());
        assertThat(actual.content()).isEqualTo(note.getContent());
    }

    @Test
    void deleteNoteByIdForCurrentUser_given_not_found_id_then_throw_note_not_found_exception() {
        var id = UUID.randomUUID();

        when(noteRepository.findByIdAndAppuserId(id, authenticatedUser.id())).thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> noteService.deleteNoteByIdForCurrentUser(id)
        ).isInstanceOf(NoteNotFoundException.class);
    }

}
