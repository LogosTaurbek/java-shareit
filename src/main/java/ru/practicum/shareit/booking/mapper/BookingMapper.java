package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookerDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.dto.NewBookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {
    public static BookingDto bookingToBookingDto(Booking booking) {
        Item item = booking.getItem();
        User booker = booking.getBooker();

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

    public static Booking newBookingRequestDtoToBooking(NewBookingRequestDto dto, Item item, User user) {
        Booking booking = new Booking();
        booking.setStart(dto.getStart());
        booking.setEnd(dto.getEnd());
        booking.setItem(item);
        booking.setBooker(user);
        return booking;
    }
}
