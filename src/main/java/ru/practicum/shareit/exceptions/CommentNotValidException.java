package ru.practicum.shareit.exceptions;

public class CommentNotValidException extends RuntimeException {
    public CommentNotValidException(String message) {
        super(message);
    }
}
