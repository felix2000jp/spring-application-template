package dev.felix2000jp.springapplicationtemplate.notes.infrastructure.api;

import dev.felix2000jp.springapplicationtemplate.notes.domain.exceptions.NoteNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(value = Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
class NoteExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(NoteExceptionHandler.class);

    @ExceptionHandler(NoteNotFoundException.class)
    ResponseEntity<ProblemDetail> handleNoteNotFoundException(NoteNotFoundException ex) {
        var problemDetails = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());

        LOGGER.warn(ex.getMessage(), ex);
        return ResponseEntity.of(problemDetails).build();
    }

}
