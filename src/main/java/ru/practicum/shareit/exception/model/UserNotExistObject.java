package ru.practicum.shareit.exception.model;

public class UserNotExistObject extends RuntimeException {
    public UserNotExistObject(String mess) {
        super(mess);
    }
}
