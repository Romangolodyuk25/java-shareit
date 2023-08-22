package ru.practicum.shareit.user.exception.model;

public class UserNotExistObject extends RuntimeException {
    public UserNotExistObject(String mess) {
        super(mess);
    }
}
