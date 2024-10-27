package dev.felix2000jp.springapplicationtemplate.notes.internal.exceptions;

public class NoteNotFoundException extends RuntimeException {

    public NoteNotFoundException() {
        super("Note could not be found");
    }

    public NoteNotFoundException(String message) {
        super(message);
    }

}
