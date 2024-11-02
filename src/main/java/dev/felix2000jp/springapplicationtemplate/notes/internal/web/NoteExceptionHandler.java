package dev.felix2000jp.springapplicationtemplate.notes.internal.web;

import dev.felix2000jp.springapplicationtemplate.notes.internal.exceptions.NoteNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
class NoteExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(NoteExceptionHandler.class);

    @ExceptionHandler(NoteNotFoundException.class)
    ResponseEntity<ProblemDetail> handleNoteNotFoundException(NoteNotFoundException ex) {
        var problemDetails = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());

        logger.warn(ex.getMessage(), ex);
        return ResponseEntity.of(problemDetails).build();
    }

}
