package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequestDto;
import ru.practicum.shareit.booking.model.BookingStatusRequestParam;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.util.HttpHeaderConstants;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto addBooking(
            @RequestHeader(HttpHeaderConstants.X_SHARER_USER_ID) int sharerUserId,
            @RequestBody NewBookingRequestDto newBookingRequestDto
    ) {
        log.info(
                "BookingController:addBooking(): запрос на создание нового бронирования {} от пользователя с ID={}",
                newBookingRequestDto,
                sharerUserId
        );
        return bookingService.addBooking(sharerUserId, newBookingRequestDto);
    }

    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDto approveBooking(
            @RequestHeader(HttpHeaderConstants.X_SHARER_USER_ID) int sharerUserId,
            @PathVariable int bookingId,
            @RequestParam("approved") boolean isApproved
    ) {
        log.info("BookingController:approveBooking(): запрос на подтверждение бронирования с ID={} от пользователя с ID={} approved={}",
                bookingId,
                sharerUserId,
                isApproved
        );
        return bookingService.approveBooking(bookingId, sharerUserId, isApproved);
    }

    @GetMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDto getBookingById(
            @RequestHeader(HttpHeaderConstants.X_SHARER_USER_ID) int sharerUserId,
            @PathVariable int bookingId
    ) {
        log.info("BookingController:getBookingById(): запрос на получение бронирования с ID={} от пользователя с ID={}",
                bookingId,
                sharerUserId
        );
        return bookingService.getBookingById(bookingId, sharerUserId);
    }

    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public List<BookingDto> getBookingsOfOwner(
            @RequestHeader(HttpHeaderConstants.X_SHARER_USER_ID) int sharerUserId,
            @RequestParam(value = "state", defaultValue = "ALL") BookingStatusRequestParam state
    ) {
        log.info("BookingController:getBookingsOfOwner(): запрос на получение всех бронирований вещей пользователя с ID={}, state={}",
                sharerUserId,
                state
        );
        return bookingService.getBookingsOfOwner(sharerUserId, state);
    }

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public List<BookingDto> getBookingsOfBooker(
            @RequestHeader(HttpHeaderConstants.X_SHARER_USER_ID) int sharerUserId,
            @RequestParam(value = "state", defaultValue = "ALL") BookingStatusRequestParam state
    ) {
        log.info("BookingController:getBookingsOfBooker(): запрос на получение всех бронирований пользователя с ID={}, state={}",
                sharerUserId,
                state
        );
        return bookingService.getBookingsOfBooker(sharerUserId, state);
    }
}
