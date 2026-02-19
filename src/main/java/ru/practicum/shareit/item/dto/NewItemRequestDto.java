package ru.practicum.shareit.item.dto;

import lombok.Data;

import java.util.Optional;

@Data
public class NewItemRequestDto {
    private String name;
    private String description;
    private Optional<Boolean> available;
}
