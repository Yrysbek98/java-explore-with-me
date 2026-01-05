package ru.yandex.practicum.ewm.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.ewm.exception.exceptionType.ConflictException;
import ru.yandex.practicum.ewm.exception.exceptionType.NotFoundException;


import java.util.HashMap;
import java.util.Map;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put("error", error.getDefaultMessage());
        });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<Map<String, Object>> handleAppExceptions(AppException ex) {
        HttpStatus status;

        if (ex instanceof NotFoundException) {
            status = HttpStatus.NOT_FOUND;
        } else if (ex instanceof ConflictException) {
            status = HttpStatus.CONFLICT;
        } else {
            status = HttpStatus.BAD_REQUEST;
        }

        Map<String, Object> body = Map.of(
                "status", status.value(),
                "error", ex.getMessage()
        );

        return new ResponseEntity<>(body, status);
    }


}



