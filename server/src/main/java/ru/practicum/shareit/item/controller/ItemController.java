package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.util.HttpHeaderConstants;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto addItem(
            @RequestHeader(HttpHeaderConstants.X_SHARER_USER_ID) int sharerUserId,
            @RequestBody NewItemRequestDto newItemRequestDto
    ) {
        log.info("ItemController:addItem(): запрос на создание нового предмета {}", newItemRequestDto);
        return itemService.addItem(sharerUserId, newItemRequestDto);
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto updateItem(
            @RequestHeader(HttpHeaderConstants.X_SHARER_USER_ID) int sharerUserId,
            @PathVariable int itemId,
            @RequestBody UpdateItemRequestDto updateItemRequestDto
    ) {
        log.info(
                "ItemController:updateItem(): запрос на обновление предмета ID={} от пользователя ID={}. Новые данные: {}",
                itemId,
                sharerUserId,
                updateItemRequestDto
        );
        return itemService.updateItem(sharerUserId, itemId, updateItemRequestDto);
    }

    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto getItemById(
            @RequestHeader(HttpHeaderConstants.X_SHARER_USER_ID) int sharerUserId,
            @PathVariable int itemId
    ) {
        log.info("ItemController:getItemById(): запрос на получение предмета с id {} от пользователя с id {}", itemId, sharerUserId);
        return itemService.getItemById(itemId, sharerUserId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> getAllItemsFromUser(@RequestHeader(HttpHeaderConstants.X_SHARER_USER_ID) int sharerUserId) {
        log.info("ItemController:getAllItemsFromUser(): запрос на получение всех предметов пользователя с id {}", sharerUserId);
        return itemService.getAllItemsFromUser(sharerUserId);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> searchAvailableItems(
            @RequestHeader(HttpHeaderConstants.X_SHARER_USER_ID) int sharerUserId,
            @RequestParam("text") String searchString
    ) {
        log.info("ItemController:searchAvailableItems(): запрос на поиск доступных предметов по запросу {} от пользователя с id {}", searchString, sharerUserId);
        return itemService.searchAvailableItems(searchString, sharerUserId);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComment(
            @RequestHeader(HttpHeaderConstants.X_SHARER_USER_ID) int sharerUserId,
            @PathVariable int itemId,
            @RequestBody NewCommentRequestDto newCommentRequestDto
    ) {
        log.info(
                "ItemController:addComment(): запрос на добавление комментария к вещи с ID={} от пользователя с ID={}; комментарий={}",
                itemId,
                sharerUserId,
                newCommentRequestDto
        );
        return itemService.addComment(sharerUserId, itemId, newCommentRequestDto);
    }
}
