package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequestDto;
import ru.practicum.shareit.item.dto.UpdateItemRequestDto;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {

    public static ItemDto itemToItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.isAvailable());
        itemDto.setOwner(item.getOwner());
        itemDto.setRequest(item.getRequest() != null ? item.getRequest() : null);
        return itemDto;
    }

    public static Item newItemRequestDtoToItem(NewItemRequestDto newItemRequestDto) {
        Item item = new Item();
        item.setName(newItemRequestDto.getName());
        item.setDescription(newItemRequestDto.getDescription());
        if (newItemRequestDto.getAvailable().isPresent()) {
            item.setAvailable(newItemRequestDto.getAvailable().get());
        }
        return item;
    }

    public static Item updateItemFields(Item itemToUpdate, UpdateItemRequestDto updateItemRequestDto) {
        if (updateItemRequestDto.hasName()) {
            itemToUpdate.setName(updateItemRequestDto.getName());
        }
        if (updateItemRequestDto.hasDescription()) {
            itemToUpdate.setDescription(updateItemRequestDto.getDescription());
        }
        if (updateItemRequestDto.hasAvailable()) {
            itemToUpdate.setAvailable(updateItemRequestDto.getAvailable());
        }
        return itemToUpdate;
    }
}
