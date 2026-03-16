package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequestDto;
import ru.practicum.shareit.booking.model.BookingStatusRequestParam;

import java.util.List;

public interface BookingService {
    BookingDto addBooking(int bookerUserId, NewBookingRequestDto newBookingRequestDto);

    BookingDto approveBooking(int bookingId, int ownerUserId, boolean isApproved);

    BookingDto getBookingById(int bookingId, int sharerUserId);

    List<BookingDto> getBookingsOfOwner(int sharerUserId, BookingStatusRequestParam state);

    List<BookingDto> getBookingsOfBooker(int bookerId, BookingStatusRequestParam state);
}
