package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.request.model.ItemRequest;

@Data
public class ItemDto {
    private int id;
    private String name;
    private String description;
    private Boolean available;
    private int owner;
    private ItemRequest request;
}
