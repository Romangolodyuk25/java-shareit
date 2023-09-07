package ru.practicum.shareit.exception;

public class StatusAlreadyApprovedException extends RuntimeException {
    public StatusAlreadyApprovedException(String mes) {
        super(mes);
    }
}
