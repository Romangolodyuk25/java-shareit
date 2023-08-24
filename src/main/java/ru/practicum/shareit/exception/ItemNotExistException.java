package ru.practicum.shareit.exception;

public class ItemNotExistException extends RuntimeException {
    public ItemNotExistException(String mess) {
        super(mess);
    }
}
