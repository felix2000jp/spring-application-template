package dev.felix2000jp.springapplicationtemplate.notes.internal;

import dev.felix2000jp.springapplicationtemplate.appusers.AppuserManagement;
import dev.felix2000jp.springapplicationtemplate.appusers.internal.dtos.AuthenticatedAppuserDTO;
import dev.felix2000jp.springapplicationtemplate.notes.internal.dtos.CreateNoteDTO;
import dev.felix2000jp.springapplicationtemplate.notes.internal.dtos.NoteDTO;
import dev.felix2000jp.springapplicationtemplate.notes.internal.dtos.NoteListDTO;
import dev.felix2000jp.springapplicationtemplate.notes.internal.dtos.UpdateNoteDTO;
import dev.felix2000jp.springapplicationtemplate.notes.internal.exceptions.NoteNotFoundException;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NoteServiceTest {

    @Spy
    private NoteMapper noteMapper = new NoteMapperImpl();
    @Mock
    private NoteRepository noteRepository;
    @Mock
    private AppuserManagement appuserManagement;
    @InjectMocks
    private NoteService noteService;

    private Note note;
    private NoteDTO noteDTO;
    private AuthenticatedAppuserDTO authenticatedAppuserDTO;

    @BeforeEach
    void setUp() {
        note = new Note(UUID.randomUUID(), "title", "content", UUID.randomUUID());
        noteDTO = new NoteDTO(note.getId(), note.getTitle(), note.getContent());
        authenticatedAppuserDTO = new AuthenticatedAppuserDTO(note.getAppuserId(), "username", Set.of("APPLICATION"));
    }

    @Test
    void findAll_should_return_notes_when_notes_are_found() {
        when(appuserManagement.getAuthenticatedAppuserDTO()).thenReturn(authenticatedAppuserDTO);
        when(noteRepository.findByAppuserId(authenticatedAppuserDTO.id())).thenReturn(List.of(note));

        var actual = noteService.findAll();

        var expected = new NoteListDTO(List.of(noteDTO));
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void findAll_should_return_empty_when_notes_are_not_found() {
        when(appuserManagement.getAuthenticatedAppuserDTO()).thenReturn(authenticatedAppuserDTO);
        when(noteRepository.findByAppuserId(authenticatedAppuserDTO.id())).thenReturn(List.of());

        var actual = noteService.findAll();

        var expected = new NoteListDTO(List.of());
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void findById_should_return_note_when_note_is_found() {
        when(appuserManagement.getAuthenticatedAppuserDTO()).thenReturn(authenticatedAppuserDTO);
        when(noteRepository.findByIdAndAppuserId(note.getId(), authenticatedAppuserDTO.id())).thenReturn(Optional.of(note));

        var actual = noteService.find(note.getId());

        assertThat(actual).usingRecursiveComparison().isEqualTo(noteDTO);
    }

    @Test
    void findById_should_throw_not_found_when_note_is_not_found() {
        when(appuserManagement.getAuthenticatedAppuserDTO()).thenReturn(authenticatedAppuserDTO);
        when(noteRepository.findByIdAndAppuserId(note.getId(), authenticatedAppuserDTO.id())).thenReturn(Optional.empty());

        var actual = catchThrowable(() -> noteService.find(note.getId()));

        assertThat(actual).isInstanceOf(NoteNotFoundException.class);
    }

    @Test
    void create_should_return_note_when_note_is_created() {
        when(appuserManagement.verifyAuthenticatedAppuserDTO()).thenReturn(authenticatedAppuserDTO);
        var createNoteDTO = new CreateNoteDTO(note.getTitle(), note.getContent());

        when(noteRepository.save(any(Note.class))).thenReturn(note);

        var actual = noteService.create(createNoteDTO);

        assertThat(actual).usingRecursiveComparison().isEqualTo(noteDTO);
    }

    @Test
    void update_should_return_note_when_note_is_updated() {
        when(appuserManagement.verifyAuthenticatedAppuserDTO()).thenReturn(authenticatedAppuserDTO);
        var updateNoteDTO = new UpdateNoteDTO("new title", "new content");

        when(noteRepository.findByIdAndAppuserId(note.getId(), authenticatedAppuserDTO.id())).thenReturn(Optional.of(note));
        when(noteRepository.save(any(Note.class))).thenReturn(note);

        var actual = noteService.update(note.getId(), updateNoteDTO);

        var expectedNote = new NoteDTO(note.getId(), "new title", "new content");
        assertThat(actual).usingRecursiveComparison().isEqualTo(expectedNote);
    }

    @Test
    void update_should_throw_not_found_when_note_is_not_found() {
        when(appuserManagement.verifyAuthenticatedAppuserDTO()).thenReturn(authenticatedAppuserDTO);
        var updateNoteDTO = new UpdateNoteDTO("new title", "new content");

        when(noteRepository.findByIdAndAppuserId(note.getId(), authenticatedAppuserDTO.id())).thenReturn(Optional.empty());

        var actual = catchThrowable(() -> noteService.update(note.getId(), updateNoteDTO));

        assertThat(actual).isInstanceOf(NoteNotFoundException.class);
    }

    @Test
    void delete_should_return_note_when_note_is_deleted() {
        when(appuserManagement.verifyAuthenticatedAppuserDTO()).thenReturn(authenticatedAppuserDTO);
        when(noteRepository.findByIdAndAppuserId(note.getId(), authenticatedAppuserDTO.id())).thenReturn(Optional.of(note));

        var actual = noteService.delete(note.getId());

        assertThat(actual).usingRecursiveComparison().isEqualTo(note);
    }

    @Test
    void delete_should_throw_not_found_when_note_is_not_found() {
        when(appuserManagement.verifyAuthenticatedAppuserDTO()).thenReturn(authenticatedAppuserDTO);
        when(noteRepository.findByIdAndAppuserId(note.getId(), authenticatedAppuserDTO.id())).thenReturn(Optional.empty());

        var actual = catchThrowable(() -> noteService.delete(note.getId()));

        assertThat(actual).isInstanceOf(NoteNotFoundException.class);
    }

}