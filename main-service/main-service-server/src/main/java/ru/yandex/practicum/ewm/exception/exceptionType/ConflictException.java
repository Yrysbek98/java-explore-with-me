package ru.yandex.practicum.ewm.exception.exceptionType;


import ru.yandex.practicum.ewm.exception.AppException;

public class ConflictException extends AppException {
    public ConflictException(String message) {
        super(message);
    }
}
