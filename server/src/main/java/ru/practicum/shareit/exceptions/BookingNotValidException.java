package ru.practicum.shareit.exceptions;

public class BookingNotValidException extends RuntimeException {
    public BookingNotValidException(String message) {
        super(message);
    }
}
