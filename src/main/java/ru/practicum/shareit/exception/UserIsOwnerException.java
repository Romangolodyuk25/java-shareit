package ru.practicum.shareit.exception;

public class UserIsOwnerException extends RuntimeException {
    public UserIsOwnerException(String mes) {
        super(mes);
    }
}
