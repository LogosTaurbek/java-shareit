package ru.practicum.shareit.exceptions;

public class ItemNotValidException extends RuntimeException {
    public ItemNotValidException(String message) {
        super(message);
    }
}
