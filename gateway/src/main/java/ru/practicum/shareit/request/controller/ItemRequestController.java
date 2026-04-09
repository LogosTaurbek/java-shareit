package ru.practicum.shareit.request.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.util.HttpHeaderConstants;
import ru.practicum.shareit.request.ItemRequestClient;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

/**
 * TODO Sprint add-item-requests.
 */
@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> addItemRequest(
            @RequestHeader(HttpHeaderConstants.X_SHARER_USER_ID) long requesterId,
            @RequestBody @Valid NewItemRequestDto newItemRequestDto
    ) {
        log.info("ItemRequestController:addItemRequest(): запрос на создание нового запроса вещи {}", newItemRequestDto);
        return itemRequestClient.addItemRequest(requesterId, newItemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getRequestsOfUser(
            @RequestHeader(HttpHeaderConstants.X_SHARER_USER_ID) long requesterId
    ) {
        log.info("ItemRequestController:getRequestsOfUser(): запрос на получение списка запросов пользователя с id={}", requesterId);
        return itemRequestClient.getRequestsOfUser(requesterId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(
            @RequestHeader(HttpHeaderConstants.X_SHARER_USER_ID) long requesterId
    ) {
        log.info("ItemRequestController:getAllRequests(): запрос на получение всех запросов других пользователей");
        return itemRequestClient.getAllRequests(requesterId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@PathVariable int requestId) {
        log.info("ItemRequestController:getRequestById(): запрос на получение запроса с id={}", requestId);
        return itemRequestClient.getRequestById(requestId);
    }
}
