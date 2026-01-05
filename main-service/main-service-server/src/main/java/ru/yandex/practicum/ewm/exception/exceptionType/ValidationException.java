package ru.yandex.practicum.ewm.exception.exceptionType;


import ru.yandex.practicum.ewm.exception.AppException;

public class ValidationException extends AppException {
    public ValidationException(String message) {
        super(message);
    }
}
