package ru.practicum.shareit.exceptions;

public class DoesNotBelongToUserException extends RuntimeException {
    public DoesNotBelongToUserException(String message) {
        super(message);
    }
}
