package ru.yandex.practicum.ewm.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.ewm.exception.dto.ApiError;
import ru.yandex.practicum.ewm.exception.exceptionType.ConflictException;
import ru.yandex.practicum.ewm.exception.exceptionType.NotFoundException;
import ru.yandex.practicum.ewm.exception.exceptionType.ValidationException;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.List;


@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationErrors(MethodArgumentNotValidException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldError();

        String message = fieldError != null
                ? String.format("Field: %s. Error: %s. Value: %s",
                fieldError.getField(),
                fieldError.getDefaultMessage(),
                fieldError.getRejectedValue())
                : "Validation error";

        ApiError error = new ApiError(
                List.of(),
                message,
                "Incorrectly made request.",
                "BAD_REQUEST",
                LocalDateTime.now().format(FORMATTER)
        );

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(NotFoundException ex) {
        ApiError error = new ApiError(
                List.of(),
                ex.getMessage(),
                "The required object was not found.",
                "NOT_FOUND",
                LocalDateTime.now().format(FORMATTER)
        );

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiError> handleConflict(ConflictException ex) {
        ApiError error = new ApiError(
                List.of(),
                ex.getMessage(),
                "Integrity constraint has been violated.",
                "CONFLICT",
                LocalDateTime.now().format(FORMATTER)
        );

        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiError> handleValidation(ValidationException ex) {
        ApiError error = new ApiError(
                List.of(),
                ex.getMessage(),
                "For the requested operation the conditions are not met.",
                "FORBIDDEN",
                LocalDateTime.now().format(FORMATTER)
        );

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(Exception ex) {
        ApiError error = new ApiError(
                List.of(ex.getClass().getName()),
                ex.getMessage(),
                "An unexpected error occurred.",
                "INTERNAL_SERVER_ERROR",
                LocalDateTime.now().format(FORMATTER)
        );

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}


