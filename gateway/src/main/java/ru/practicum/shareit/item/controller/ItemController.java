package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.dto.NewCommentRequestDto;
import ru.practicum.shareit.item.dto.NewItemRequestDto;
import ru.practicum.shareit.item.dto.UpdateItemRequestDto;
import ru.practicum.shareit.item.util.HttpHeaderConstants;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(
            @RequestHeader(HttpHeaderConstants.X_SHARER_USER_ID) long userId,
            @RequestBody @Valid NewItemRequestDto newItemRequestDto
    ) {
        log.info(
                "ItemController:addItem(): запрос на создание нового предмета {} от пользователя с ID={}",
                newItemRequestDto,
                userId
        );
        return itemClient.addItem(userId, newItemRequestDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(
            @RequestHeader(HttpHeaderConstants.X_SHARER_USER_ID) long userId,
            @PathVariable int itemId,
            @RequestBody @Valid UpdateItemRequestDto updateItemRequestDto
    ) {
        log.info(
                "ItemController:updateItem(): запрос на обновление предмета ID={} от пользователя ID={}. Новые данные: {}",
                itemId,
                userId,
                updateItemRequestDto
        );
        return itemClient.updateItem(userId, itemId, updateItemRequestDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(
            @RequestHeader(HttpHeaderConstants.X_SHARER_USER_ID) long userId,
            @PathVariable int itemId
    ) {
        log.info(
                "ItemController:getItemById(): запрос на получение предмета с id {} от пользователя с id {}",
                itemId,
                userId
        );
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsFromUser(@RequestHeader(HttpHeaderConstants.X_SHARER_USER_ID) long userId) {
        log.info("ItemController:getAllItemsFromUser(): запрос на получение всех предметов пользователя с id {}", userId);
        return itemClient.getAllItemsFromUser(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchAvailableItems(
            @RequestHeader(HttpHeaderConstants.X_SHARER_USER_ID) long userId,
            @RequestParam("text") String searchString
    ) {
        log.info(
                "ItemController:searchAvailableItems(): запрос на поиск доступных предметов по запросу {} от пользователя с id {}",
                searchString,
                userId
        );
        return itemClient.searchAvailableItems(userId, searchString);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(
            @RequestHeader(HttpHeaderConstants.X_SHARER_USER_ID) long userId,
            @PathVariable int itemId,
            @RequestBody @Valid NewCommentRequestDto newCommentRequestDto
    ) {
        log.info(
                "ItemController:addComment(): запрос на добавление комментария к вещи с ID={} от пользователя с ID={}; комментарий={}",
                itemId,
                userId,
                newCommentRequestDto
        );
        return itemClient.addComment(userId, itemId, newCommentRequestDto);
    }
}
