package ru.practicum.shareit.exception;

public class ItemRequestNotExist extends RuntimeException {
    public ItemRequestNotExist(String mes) {
        super(mes);
    }
}
