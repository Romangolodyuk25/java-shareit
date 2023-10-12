package ru.practicum.shareit.exception;

public class BookingNotExistException extends RuntimeException {
    public BookingNotExistException(String mes) {
        super(mes);
    }
}
