package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequestDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.BookingStatusRequestParam;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.BookingNotValidException;
import ru.practicum.shareit.exceptions.DoesNotBelongToUserException;
import ru.practicum.shareit.exceptions.ItemNotAvailableException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingDto addBooking(int bookerUserId, NewBookingRequestDto newBookingRequestDto) {
        User bookerUser = findUserOrThrow(bookerUserId);
        log.info(
                "BookingServiceImpl:addBooking(): запрос на создание нового бронирования {} от пользователя с id={}",
                newBookingRequestDto,
                bookerUser
        );

        Item item = findItemOrThrow(newBookingRequestDto.getItemId());

        validateNewBookingRequestDto(newBookingRequestDto);

        Booking newBooking = BookingMapper.newBookingRequestDtoToBooking(newBookingRequestDto, item, bookerUser);
        newBooking.setBooker(bookerUser);
        newBooking.setItem(item);
        newBooking.setStatus(BookingStatus.WAITING);

        Booking createdBooking = bookingRepository.save(newBooking);
        log.info("BookingServiceImpl:addBooking(): создано новое бронирование {}", createdBooking);

        return BookingMapper.bookingToBookingDto(createdBooking);
    }

    @Override
    public BookingDto approveBooking(int bookingId, int ownerUserId, boolean isApproved) {
        log.info(
                "BookingServiceImpl:approveBooking(): запрос на подтверждение бронирования с ID={} от пользователя с ID={} approved={}",
                bookingId,
                ownerUserId,
                isApproved
        );
        Booking booking = fingBookingOrThrow(bookingId);
        Item item = booking.getItem();
        int itemId = item.getId();

        if (!itemBelongsToUser(itemId, ownerUserId)) {
            throw new DoesNotBelongToUserException("Вещь с ID={" + itemId + "} не принадлежит пользователю с ID={" + ownerUserId + "}");
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new BookingNotValidException("Нельзя изменить статус бронирования, которое уже обработано");
        }
        booking.setStatus(isApproved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking savedBooking = bookingRepository.save(booking);

        log.info(
                "BookingServiceImpl:approveBooking(): выполнен запрос на подтверждение бронирования с ID={} от пользователя с ID={} approved={}",
                bookingId,
                ownerUserId,
                isApproved
        );
        return BookingMapper.bookingToBookingDto(savedBooking);
    }

    @Override
    public BookingDto getBookingById(int bookingId, int sharerUserId) {
        log.info(
                "BookingServiceImpl:getBookingById(): запрос на получение бронирования с ID={} от пользователя с ID={}",
                bookingId,
                sharerUserId
        );
        Booking booking = fingBookingOrThrow(bookingId);
        Item item = booking.getItem();
        User booker = booking.getBooker();

        if (sharerUserId != booker.getId() && sharerUserId != item.getOwner().getId()) {
            throw new DoesNotBelongToUserException(
                    "Запрошенное бронирование с ID=" + bookingId + " никак не связано с пользователем с ID=" + sharerUserId
            );
        }
        return BookingMapper.bookingToBookingDto(booking);
    }

    @Override
    public List<BookingDto> getBookingsOfOwner(int ownerId, BookingStatusRequestParam state) {
        log.info(
                "BookingServiceImpl:getBookingsOfOwner(): запрос на получение всех бронирований вещей пользователя с ID={}, state={}",
                ownerId,
                state
        );
        userRepository.findById(ownerId).orElseThrow();
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findAllByOwnerId(ownerId);
            case CURRENT -> bookingRepository.findCurrentByOwnerId(ownerId, now);
            case PAST -> bookingRepository.findPastByOwnerId(ownerId, now);
            case FUTURE -> bookingRepository.findFutureByOwnerId(ownerId, now);
            case WAITING -> bookingRepository.findByOwnerIdAndStatus(ownerId, BookingStatus.WAITING);
            case REJECTED -> bookingRepository.findByOwnerIdAndStatus(ownerId, BookingStatus.REJECTED);
        };
        return bookings.stream()
                .map(BookingMapper::bookingToBookingDto)
                .toList();
    }

    @Override
    public List<BookingDto> getBookingsOfBooker(int bookerId, BookingStatusRequestParam state) {
        log.info(
                "BookingController:getBookingsOfBooker(): запрос на получение всех бронирований пользователя с ID={}, state={}",
                bookerId,
                state
        );
        userRepository.findById(bookerId).orElseThrow();
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findAllByBookerOrderByStartDesc(bookerId);
            case CURRENT ->
                    bookingRepository.findAllByBookerAndStartBeforeAndEndAfterOrderByStartDesc(bookerId, now, now);
            case PAST -> bookingRepository.findAllByBookerAndEndBeforeOrderByStartDesc(bookerId, now);
            case FUTURE -> bookingRepository.findAllByBookerAndStartAfterOrderByStartDesc(bookerId, now);
            case WAITING -> bookingRepository.findAllByBookerAndStatusOrderByStartDesc(bookerId, BookingStatus.WAITING);
            case REJECTED ->
                    bookingRepository.findAllByBookerAndStatusOrderByStartDesc(bookerId, BookingStatus.REJECTED);
        };
        return bookings.stream()
                .map(BookingMapper::bookingToBookingDto)
                .toList();
    }

    private User findUserOrThrow(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователя с ID " + userId + " не существует"));
    }

    private Item findItemOrThrow(int itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Вещи с ID " + itemId + " не существует"));
    }

    private Booking fingBookingOrThrow(int bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NoSuchElementException("Вещи с ID " + bookingId + " не существует"));
    }

    private void checkIfItemAvailable(int itemId) {
        if (!itemRepository.findById(itemId).orElseThrow().isAvailable()) {
            throw new ItemNotAvailableException("Вещь с ID={" + itemId + "} недоступна");
        }
    }

    private void validateNewBookingRequestDto(NewBookingRequestDto newBookingRequestDto) {
        if (newBookingRequestDto.getStart().isBefore(LocalDateTime.now())) {
            throw new BookingNotValidException("Дата начала бронирования находится в прошлом.");
        }
        if (!newBookingRequestDto.getEnd().isAfter(newBookingRequestDto.getStart())) {
            throw new BookingNotValidException("Дата окончания бронирования находится раньше даты начала бронирования.");
        }
        if (newBookingRequestDto.getItemId() == null) {
            throw new BookingNotValidException("Вещь не может быть равна null");
        }
        checkIfItemAvailable(newBookingRequestDto.getItemId());
    }

    private boolean itemBelongsToUser(int itemId, int userId) {
        Item item = itemRepository.findById(itemId).orElseThrow();
        return item.getOwner().getId() == userId;
    }
}