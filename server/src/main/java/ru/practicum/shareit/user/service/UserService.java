package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.NewUserRequestDto;
import ru.practicum.shareit.user.dto.UpdateUserRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {
    UserDto addUser(NewUserRequestDto newUserRequestDto);

    UserDto getUserById(int userId);

    void validateNewUserRequestDto(NewUserRequestDto newUserRequestDto);

    UserDto updateUser(int userId, UpdateUserRequestDto updateUserRequestDto);

    void deleteUser(int userId);
}
