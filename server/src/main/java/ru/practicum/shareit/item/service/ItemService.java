package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {

    ItemDto addItem(int sharerUserId, NewItemRequestDto newItemRequestDto);

    void validateNewItemRequestDto(NewItemRequestDto newItemRequestDto);

    ItemDto updateItem(int sharerUserId, int itemId, UpdateItemRequestDto updateItemRequestDto);

    ItemDto getItemById(int itemId, int requestingUserId);

    List<ItemDto> getAllItemsFromUser(int sharerUserId);

    List<ItemDto> searchAvailableItems(String searchString, int requestingUserId);

    CommentDto addComment(int sharerUserId, int itemId, NewCommentRequestDto newCommentRequestDto);
}
