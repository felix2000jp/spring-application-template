package dev.felix2000jp.springapplicationtemplate.notes;

import dev.felix2000jp.springapplicationtemplate.notes.internal.NoteService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NoteManagementTest {

    @Mock
    private NoteService noteService;
    @InjectMocks
    private NoteManagement noteManagement;

}
