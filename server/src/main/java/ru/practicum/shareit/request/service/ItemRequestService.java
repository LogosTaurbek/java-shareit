package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto addItemRequest(int requesterId, NewItemRequestDto newItemRequestDto);

    List<ItemRequestDto> getRequestsOfUser(int requesterId);

    List<ItemRequestDto> getAllRequests(int requesterId);

    ItemRequestDto getRequestById(int requestId);
}
