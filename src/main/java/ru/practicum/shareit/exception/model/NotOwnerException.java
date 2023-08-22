package ru.practicum.shareit.exception.model;

public class NotOwnerException extends RuntimeException {
    public NotOwnerException(String mes) {
        super(mes);
    }
}
