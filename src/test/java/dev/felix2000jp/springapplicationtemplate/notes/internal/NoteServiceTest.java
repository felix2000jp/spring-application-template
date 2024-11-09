package dev.felix2000jp.springapplicationtemplate.notes.internal;

import dev.felix2000jp.springapplicationtemplate.appusers.AppuserManagement;
import dev.felix2000jp.springapplicationtemplate.notes.internal.dtos.CreateNoteDto;
import dev.felix2000jp.springapplicationtemplate.notes.internal.dtos.NoteDto;
import dev.felix2000jp.springapplicationtemplate.notes.internal.dtos.NoteListDto;
import dev.felix2000jp.springapplicationtemplate.notes.internal.dtos.UpdateNoteDto;
import dev.felix2000jp.springapplicationtemplate.notes.internal.exceptions.NoteNotFoundException;
import dev.felix2000jp.springapplicationtemplate.shared.AppuserPrincipal;
import dev.felix2000jp.springapplicationtemplate.shared.AuthorityValue;
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

    private AppuserPrincipal principal;
    private Note note;
    private NoteDto noteDto;

    @BeforeEach
    void setUp() {
        principal = new AppuserPrincipal(UUID.randomUUID(), "username", Set.of(AuthorityValue.APPLICATION));
        note = new Note(UUID.randomUUID(), "title", "content", principal.id());
        noteDto = new NoteDto(note.getId(), note.getTitle(), note.getContent());
    }

    @Test
    void findAll_should_return_notes_when_notes_are_found() {
        when(noteRepository.findByAppuserId(principal.id())).thenReturn(List.of(note));

        var actual = noteService.findAll(principal);

        var expected = new NoteListDto(List.of(noteDto));
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void findAll_should_return_empty_when_notes_are_not_found() {
        when(noteRepository.findByAppuserId(principal.id())).thenReturn(List.of());

        var actual = noteService.findAll(principal);

        var expected = new NoteListDto(List.of());
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void findById_should_return_note_when_note_is_found() {
        when(noteRepository.findByIdAndAppuserId(note.getId(), principal.id())).thenReturn(Optional.of(note));

        var actual = noteService.findById(principal, note.getId());

        assertThat(actual).usingRecursiveComparison().isEqualTo(noteDto);
    }

    @Test
    void findById_should_throw_not_found_when_note_is_not_found() {
        when(noteRepository.findByIdAndAppuserId(note.getId(), principal.id())).thenReturn(Optional.empty());

        var actual = catchThrowable(() -> noteService.findById(principal, note.getId()));

        assertThat(actual).isInstanceOf(NoteNotFoundException.class);
    }

    @Test
    void create_should_return_note_when_note_is_created() {
        var createNoteDto = new CreateNoteDto(note.getTitle(), note.getContent());

        when(noteRepository.save(any(Note.class))).thenReturn(note);

        var actual = noteService.create(principal, createNoteDto);

        assertThat(actual).usingRecursiveComparison().isEqualTo(noteDto);
    }

    @Test
    void update_should_return_note_when_note_is_updated() {
        var updateNoteDto = new UpdateNoteDto("new title", "new content");

        when(noteRepository.findByIdAndAppuserId(note.getId(), principal.id())).thenReturn(Optional.of(note));
        when(noteRepository.save(any(Note.class))).thenReturn(note);

        var actual = noteService.update(principal, note.getId(), updateNoteDto);

        var expectedNote = new NoteDto(note.getId(), "new title", "new content");
        assertThat(actual).usingRecursiveComparison().isEqualTo(expectedNote);
    }

    @Test
    void update_should_throw_not_found_when_note_is_not_found() {
        var updateNoteDto = new UpdateNoteDto("new title", "new content");

        when(noteRepository.findByIdAndAppuserId(note.getId(), principal.id())).thenReturn(Optional.empty());

        var actual = catchThrowable(() -> noteService.update(principal, note.getId(), updateNoteDto));

        assertThat(actual).isInstanceOf(NoteNotFoundException.class);
    }

    @Test
    void delete_should_return_note_when_note_is_deleted() {
        when(noteRepository.findByIdAndAppuserId(note.getId(), principal.id())).thenReturn(Optional.of(note));

        var actual = noteService.delete(principal, note.getId());

        assertThat(actual).usingRecursiveComparison().isEqualTo(note);
    }

    @Test
    void delete_should_throw_not_found_when_note_is_not_found() {
        when(noteRepository.findByIdAndAppuserId(note.getId(), principal.id())).thenReturn(Optional.empty());

        var actual = catchThrowable(() -> noteService.delete(principal, note.getId()));

        assertThat(actual).isInstanceOf(NoteNotFoundException.class);
    }

}