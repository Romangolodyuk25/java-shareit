package ru.practicum.shareit.user.exception.model;

public class ItemNotExistException extends RuntimeException {
    public ItemNotExistException(String mess) {
        super(mess);
    }
}
