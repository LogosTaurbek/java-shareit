package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequestDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequestDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.NewUserRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplIntegrationTest {

    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;

    @Test
    void addBooking_and_getBookingById() {
        NewUserRequestDto ownerRequest = new NewUserRequestDto();
        ownerRequest.setName("Owner");
        ownerRequest.setEmail("owner@example.com");
        UserDto owner = userService.addUser(ownerRequest);

        NewUserRequestDto bookerRequest = new NewUserRequestDto();
        bookerRequest.setName("Booker");
        bookerRequest.setEmail("booker@example.com");
        UserDto booker = userService.addUser(bookerRequest);

        NewItemRequestDto itemRequest = new NewItemRequestDto();
        itemRequest.setName("Item");
        itemRequest.setDescription("Description");
        itemRequest.setAvailable(Optional.of(true));
        ItemDto item = itemService.addItem(owner.getId(), itemRequest);

        NewBookingRequestDto bookingRequest = new NewBookingRequestDto();
        bookingRequest.setItemId(item.getId());
        bookingRequest.setStart(LocalDateTime.now().plusDays(1));
        bookingRequest.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto savedBooking = bookingService.addBooking(booker.getId(), bookingRequest);

        assertThat(savedBooking.getId()).isNotZero();
        assertThat(savedBooking.getStatus()).isEqualTo(BookingStatus.WAITING);

        BookingDto foundBooking = bookingService.getBookingById(savedBooking.getId(), booker.getId());
        assertThat(foundBooking).isEqualTo(savedBooking);
    }
}
