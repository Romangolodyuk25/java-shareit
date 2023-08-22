package ru.practicum.shareit.exception.model;

public class ItemNotExistException extends RuntimeException {
    public ItemNotExistException(String mess) {
        super(mess);
    }
}
