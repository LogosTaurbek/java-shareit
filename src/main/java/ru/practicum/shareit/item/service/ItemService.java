package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequestDto;
import ru.practicum.shareit.item.dto.UpdateItemRequestDto;

import java.util.List;

public interface ItemService {

    ItemDto addItem(int sharerUserId, NewItemRequestDto newItemRequestDto);

    void validateNewItemRequestDto(NewItemRequestDto newItemRequestDto);

    ItemDto updateItem(int sharerUserId, int itemId, UpdateItemRequestDto updateItemRequestDto);

    ItemDto getItemById(int itemId);

    List<ItemDto> getAllItemsFromUser(int sharerUserId);

    List<ItemDto> searchAvailableItems(String searchString);
}
