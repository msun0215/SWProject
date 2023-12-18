package com.example.BoardDBRestAPIBySpring.controller.exception;

import com.example.BoardDBRestAPIBySpring.response.ErrorResponse;
import java.util.stream.Collectors;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String bindingResultMessage = e.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));

        final ErrorResponse errorResponse = ErrorResponse.builder()
                .message(bindingResultMessage)
                .build();

        return ResponseEntity.status(e.getStatusCode()).body(errorResponse);
    }
}
