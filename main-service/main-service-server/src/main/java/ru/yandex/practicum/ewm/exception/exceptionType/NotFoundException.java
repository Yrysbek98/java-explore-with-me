package ru.yandex.practicum.ewm.exception.exceptionType;


import ru.yandex.practicum.ewm.exception.AppException;

public class NotFoundException extends AppException {
    public NotFoundException(String message) {
        super(message);
    }
}
