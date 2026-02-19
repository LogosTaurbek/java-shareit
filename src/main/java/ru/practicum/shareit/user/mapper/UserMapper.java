package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.dto.NewUserRequestDto;
import ru.practicum.shareit.user.dto.UpdateUserRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public class UserMapper {
    public static UserDto userToUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        return userDto;
    }

    public static User newUserRequestDtoToUser(NewUserRequestDto newUserRequestDto) {
        User user = new User();
        user.setName(newUserRequestDto.getName());
        user.setEmail(newUserRequestDto.getEmail());
        return user;
    }

    public static User updateUserFields(User user, UpdateUserRequestDto updateUserRequestDto) {
        if (updateUserRequestDto.hasName()) {
            user.setName(updateUserRequestDto.getName());
        }
        if (updateUserRequestDto.hasEmail()) {
            user.setEmail(updateUserRequestDto.getEmail());
        }
        return user;
    }
}
