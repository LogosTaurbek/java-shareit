package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

public class ItemMapper {


    public static ItemDto itemToItemDto(
            Item item,
            Booking previousBooking,
            Booking nextBooking,
            List<Comment> comments,
            List<String> commentAuthorNames
    ) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.isAvailable());
        itemDto.setOwner(item.getOwner().getId());
        itemDto.setRequest(item.getRequestId());
        if (previousBooking != null) {
            itemDto.setLastBooking(new BookingShortDto(
                    previousBooking.getId(), previousBooking.getStart(),
                    previousBooking.getEnd(), previousBooking.getId()));
        }
        if (nextBooking != null) {
            itemDto.setNextBooking(new BookingShortDto(
                    nextBooking.getId(), nextBooking.getStart(),
                    nextBooking.getEnd(), nextBooking.getId()));
        }
        List<CommentDto> commentDtos = new ArrayList<>();
        for (int i = 0; i < comments.size(); i++) {
            commentDtos.add(CommentMapper.commentToCommentDto(comments.get(i), commentAuthorNames.get(i)));
        }
        itemDto.setComments(commentDtos);
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
