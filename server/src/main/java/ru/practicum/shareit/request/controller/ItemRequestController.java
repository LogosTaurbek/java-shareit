package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDto addItemRequest(
            @RequestHeader("X-Sharer-User-Id") int requesterId,
            @RequestBody NewItemRequestDto newItemRequestDto
    ) {
        log.info("Server ItemRequestController:addItemRequest(): запрос на создание нового запроса вещи {}", newItemRequestDto);
        return itemRequestService.addItemRequest(requesterId, newItemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getRequestsOfUser(
            @RequestHeader("X-Sharer-User-Id") int requesterId
    ) {
        log.info("Server ItemRequestController:getRequestsOfUser(): запрос на получение списка запросов пользователя с id={}", requesterId);
        return itemRequestService.getRequestsOfUser(requesterId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(
            @RequestHeader("X-Sharer-User-Id") int requesterId
    ) {
        log.info("Server ItemRequestController:getAllRequests(): запрос на получение всех запросов других пользователей");
        return itemRequestService.getAllRequests(requesterId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@PathVariable int requestId) {
        log.info("Server ItemRequestController:getRequestById(): запрос на получение запроса с id={}", requestId);
        return itemRequestService.getRequestById(requestId);
    }
}
