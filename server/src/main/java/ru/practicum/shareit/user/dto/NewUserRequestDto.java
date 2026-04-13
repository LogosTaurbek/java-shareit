package ru.practicum.shareit.user.dto;

import lombok.Data;

@Data
public class NewUserRequestDto {
    private String name;
    private String email;
}
