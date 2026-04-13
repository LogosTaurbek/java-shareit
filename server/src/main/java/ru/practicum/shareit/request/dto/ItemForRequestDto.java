package ru.practicum.shareit.request.dto;

import lombok.Data;

@Data
public class ItemForRequestDto {
    private int id;
    private String name;
    private Integer owner;
}
