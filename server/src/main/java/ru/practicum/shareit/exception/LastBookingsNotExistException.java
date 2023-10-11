package ru.practicum.shareit.exception;

public class LastBookingsNotExistException extends RuntimeException {
    public LastBookingsNotExistException(String mess) {
        super(mess);
    }
}
