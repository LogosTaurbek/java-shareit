package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.CommentNotPossibleException;
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
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

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
        if (userRepository.findById(sharerUserId).isPresent()) {
            newItem.setOwnerId(sharerUserId);
        } else {
            throw new NoSuchElementException("Пользователя с ID " + sharerUserId + " не существует");
        }
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
        checkIfItemBelongsToUser(itemId, sharerUserId);
        Item itemToUpdate = itemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Предмета с ID " + itemId + " не существует"));
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
        List<Item> items = itemRepository.findAllByOwnerId(sharerUserId);
        return items.stream()
                .map(item -> createItemDto(item, sharerUserId))
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
        if (!bookingRepository.existsByBookerAndItemAndEndBeforeAndStatus(sharerUserId, itemId, LocalDateTime.now(), BookingStatus.APPROVED)) {
            throw new CommentNotPossibleException("Комментирование недоступно.");
        }
        Comment newComment = new Comment();
        if (newCommentRequestDto.getText().length() > 1000) {
            newCommentRequestDto.setText(newCommentRequestDto.getText().substring(0, 1000));
        }
        newComment.setText(newCommentRequestDto.getText());
        newComment.setItem(itemId);
        newComment.setAuthor(sharerUserId);
        newComment.setCreated(LocalDateTime.now());
        Comment createdComment = commentRepository.save(newComment);
        User commentAuthor = userRepository.findById(createdComment.getAuthor()).orElseThrow();
        return CommentMapper.commentToCommentDto(createdComment, commentAuthor.getName());
    }

    private void checkIfItemBelongsToUser(int itemId, int userId) {
        int ownerId = itemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Предмета с ID " + itemId + " не существует"))
                .getOwnerId();
        if (userId != ownerId) {
            System.out.println("USERID" + userId + " Onwerid" + ownerId);
            throw new DoesNotBelongToUserException(
                    "Предмет ID=%s не принадлежит пользователю ID=%s".formatted(itemId, userId)
            );
        }
    }

    private ItemDto createItemDto(Item item, int requestingUserId) {
        LocalDateTime now = LocalDateTime.now();
        Booking lastBooking = null;
        Booking nextBooking = null;
        if (requestingUserId == item.getOwnerId()) {
            lastBooking = bookingRepository.findLastBookingForItem(item.getId(), now);
            nextBooking = bookingRepository.findNextBookingForItem(item.getId(), now);
        }
        List<Comment> comments = commentRepository.findAllByItem(item.getId());
        List<String> commentAuthorNames = comments.stream()
                .map(Comment::getAuthor)
                .map(authorId -> userRepository.findById(authorId).orElseThrow())
                .map(User::getName)
                .toList();
        return ItemMapper.itemToItemDto(item, lastBooking, nextBooking, comments, commentAuthorNames);
    }
}
