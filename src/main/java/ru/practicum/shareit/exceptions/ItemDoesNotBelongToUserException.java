package ru.practicum.shareit.exceptions;

public class ItemDoesNotBelongToUserException extends RuntimeException {

    public ItemDoesNotBelongToUserException(String message) {
        super(message);
    }
}
