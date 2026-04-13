package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NewBookingRequestDto {
    @NotNull(message = "Дата начала бронирования не может быть null")
    @FutureOrPresent(message = "Дата начала бронирования не может быть в прошлом")
    private LocalDateTime start;

    @NotNull(message = "Дата окончания бронирования не может быть null")
    @Future(message = "Дата окончания бронирования должна быть в будущем")
    private LocalDateTime end;

    @NotNull(message = "ID вещи не может быть null")
    private Integer itemId;
}
