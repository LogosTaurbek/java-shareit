package ru.practicum.shareit.request.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ItemRequestDto {
    private int id;
    private String description;
    private int requesterId;
    private LocalDateTime created;
    private List<ItemForRequestDto> items;
}
