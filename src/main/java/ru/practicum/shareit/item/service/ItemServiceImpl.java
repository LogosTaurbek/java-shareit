package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.CommentNotPossibleException;
import ru.practicum.shareit.exceptions.CommentNotValidException;
import ru.practicum.shareit.exceptions.DoesNotBelongToUserException;
import ru.practicum.shareit.exceptions.ItemNotValidException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto addItem(int sharerUserId, NewItemRequestDto newItemRequestDto) {
        log.info(
                "ItemServiceImpl:addItem(): запрос на создание нового предмета {} от пользователя с id={}",
                newItemRequestDto,
                sharerUserId
        );
        validateNewItemRequestDto(newItemRequestDto);
        Item newItem = ItemMapper.newItemRequestDtoToItem(newItemRequestDto);
        User sharerUser = findUserOrThrow(sharerUserId);

        newItem.setOwner(sharerUser);

        Item createdItem = itemRepository.save(newItem);
        log.info("ItemServiceImpl:addItem(): создан новый предмет {}", createdItem);
        return createItemDto(createdItem, sharerUserId);
    }

    @Override
    public void validateNewItemRequestDto(NewItemRequestDto newItemRequestDto) {
        if (newItemRequestDto.getName() == null || newItemRequestDto.getName().isBlank()) {
            throw new ItemNotValidException("Имя вещи не может быть пустым или null");
        }
        if (newItemRequestDto.getDescription() == null || newItemRequestDto.getDescription().isBlank()) {
            throw new ItemNotValidException("Описание вещи не может быть пустым или null");
        }
        if (newItemRequestDto.getAvailable().isEmpty()) {
            throw new ItemNotValidException("У вещи отсутствует информация о доступности для аренды");
        }
    }

    @Override
    public ItemDto updateItem(int sharerUserId, int itemId, UpdateItemRequestDto updateItemRequestDto) {
        log.info(
                "ItemServiceImpl:updateItem(): запрос на обновление предмета id={} от пользователя id={}, новые данные {}",
                itemId,
                sharerUserId,
                updateItemRequestDto
        );
        Item itemToUpdate = findItemOrThrow(itemId);
        checkIfItemBelongsToUser(itemToUpdate, sharerUserId);

        Item updatedItem = ItemMapper.updateItemFields(itemToUpdate, updateItemRequestDto);
        updatedItem = itemRepository.save(updatedItem);
        log.info("ItemServiceImpl:updateItem(): предмет id={} отредактирован, новые данные: {}", itemId, updatedItem);
        return createItemDto(updatedItem, sharerUserId);
    }

    @Override
    public ItemDto getItemById(int itemId, int requestingUserId) {
        log.info("ItemServiceImpl:getItemById(): запрос на получение предмета с id {}", itemId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Предмета с ID " + itemId + " не существует"));
        return createItemDto(item, requestingUserId);
    }

    @Override
    public List<ItemDto> getAllItemsFromUser(int sharerUserId) {
        log.info("ItemServiceImpl:getAllItemsFromUser(): запрос на получение всех предметов пользователя с id {}", sharerUserId);

        // Запрос 1: все вещи пользователя
        List<Item> items = itemRepository.findAllByOwnerId(sharerUserId);
        if (items.isEmpty()) return List.of();

        // Запрос 2: все подтверждённые брони по этим вещам
        Map<Integer, List<Booking>> bookingsByItemId = bookingRepository
                .findAllByItemsAndStatusApproved(items)
                .stream()
                .collect(Collectors.groupingBy(b -> b.getItem().getId()));
        Map<Integer, List<Comment>> commentsByItemId = commentRepository
                .findAllByItemIn(items)
                .stream()
                .collect(Collectors.groupingBy(c -> c.getItem().getId()));

        LocalDateTime now = LocalDateTime.now();

        return items.stream()
                .map(item -> {
                    List<Booking> itemBookings = bookingsByItemId.getOrDefault(item.getId(), List.of());

                    Booking lastBooking = itemBookings.stream()
                            .filter(b -> !b.getStart().isAfter(now))
                            .max(Comparator.comparing(Booking::getStart))
                            .orElse(null);

                    Booking nextBooking = itemBookings.stream()
                            .filter(b -> b.getStart().isAfter(now))
                            .min(Comparator.comparing(Booking::getStart))
                            .orElse(null);

                    List<Comment> comments = commentsByItemId.getOrDefault(item.getId(), List.of());
                    List<String> commentAuthorNames = comments.stream()
                            .map(c -> c.getAuthor().getName())
                            .toList();

                    return ItemMapper.itemToItemDto(item, lastBooking, nextBooking, comments, commentAuthorNames);
                })
                .toList();
    }

    @Override
    public List<ItemDto> searchAvailableItems(String searchString, int requestingUserId) {
        log.info("ItemServiceImpl:searchAvailableItems(): запрос на поиск доступных предметов по запросу {}", searchString);
        if (searchString == null || searchString.isEmpty()) {
            return new ArrayList<>();
        }
        List<Item> itemSearchResults = itemRepository.searchAvailableItems(searchString);
        return itemSearchResults.stream()
                .map(item -> createItemDto(item, requestingUserId))
                .toList();
    }

    @Override
    public CommentDto addComment(int sharerUserId, int itemId, NewCommentRequestDto newCommentRequestDto) {
        log.info(
                "ItemServiceImpl:addComment(): запрос на добавление комментария к вещи с ID={} от пользователя с ID={}; комментарий={}",
                itemId,
                sharerUserId,
                newCommentRequestDto
        );
        if (newCommentRequestDto.getText() == null || newCommentRequestDto.getText().isBlank()) {
            throw new CommentNotValidException("Текст комментария не может быть пустым или null");
        }
        if (!bookingRepository.existsByBookerAndItemAndEndBeforeAndStatus(sharerUserId, itemId, LocalDateTime.now(), BookingStatus.APPROVED)) {
            throw new CommentNotPossibleException("Комментирование недоступно.");
        }

        Item item = findItemOrThrow(itemId);
        User author = findUserOrThrow(sharerUserId);

        Comment newComment = CommentMapper.newCommentRequestDtoToComment(newCommentRequestDto, item, author);
        Comment createdComment = commentRepository.save(newComment);
        return CommentMapper.commentToCommentDto(createdComment, createdComment.getAuthor().getName());
    }

    private void checkIfItemBelongsToUser(Item item, int userId) {
        int ownerId = item.getOwner().getId();
        if (userId != ownerId) {
            throw new DoesNotBelongToUserException(
                    "Предмет ID=%s не принадлежит пользователю ID=%s".formatted(item.getId(), userId)
            );
        }
    }

    private ItemDto createItemDto(Item item, int requestingUserId) {
        LocalDateTime now = LocalDateTime.now();
        Booking lastBooking = null;
        Booking nextBooking = null;
        if (requestingUserId == item.getOwner().getId()) {
            lastBooking = bookingRepository.findLastBookingForItem(item.getId(), now);
            nextBooking = bookingRepository.findNextBookingForItem(item.getId(), now);
        }
        List<Comment> comments = commentRepository.findAllByItemId(item.getId());
        List<String> commentAuthorNames = comments.stream()
                .map(Comment::getAuthor)
                .map(User::getName)
                .toList();
        return ItemMapper.itemToItemDto(item, lastBooking, nextBooking, comments, commentAuthorNames);
    }

    private User findUserOrThrow(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователя с ID " + userId + " не существует"));
    }

    private Item findItemOrThrow(int itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Вещи с ID " + itemId + " не существует"));
    }
}