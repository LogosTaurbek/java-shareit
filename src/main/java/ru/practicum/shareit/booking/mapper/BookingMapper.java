package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookerDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.dto.NewBookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {
    public static BookingDto bookingToBookingDto(Booking booking, User booker, Item item) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        BookingItemDto bookingItemDto = new BookingItemDto();
        bookingItemDto.setId(item.getId());
        bookingItemDto.setName(item.getName());
        bookingDto.setItem(bookingItemDto);
        BookerDto bookerDto = new BookerDto();
        bookerDto.setId(booker.getId());
        bookingDto.setBooker(bookerDto);
        bookingDto.setStatus(booking.getStatus());
        return bookingDto;
    }

    public static Booking newBookingRequestDtoToBooking(NewBookingRequestDto newBookingRequestDto) {
        Booking booking = new Booking();
        booking.setStart(newBookingRequestDto.getStart());
        booking.setEnd(newBookingRequestDto.getEnd());
        booking.setItem(newBookingRequestDto.getItemId());
        return booking;
    }
}
