package ru.practicum.shareit.exception;

public class UnsupportedStatusExist extends RuntimeException {
    public UnsupportedStatusExist(String error) {
        super(error);
    }
}
