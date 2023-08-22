package ru.practicum.shareit.user.exception.model;

public class NotOwnerException extends RuntimeException{
    public NotOwnerException(String mes) {
        super(mes);
    }
}
