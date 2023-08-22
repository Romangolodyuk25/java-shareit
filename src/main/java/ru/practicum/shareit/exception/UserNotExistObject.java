package ru.practicum.shareit.exception;

public class UserNotExistObject extends RuntimeException {
    public UserNotExistObject(String mess) {
        super(mess);
    }
}
