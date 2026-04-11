package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.NewUserRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplIntegrationTest {

    private final UserService userService;

    @Test
    void addUser_shouldSaveUser() {
        NewUserRequestDto request = new NewUserRequestDto();
        request.setName("Integration User");
        request.setEmail("integration@example.com");

        UserDto savedUser = userService.addUser(request);

        assertThat(savedUser.getId()).isNotZero();
        assertThat(savedUser.getName()).isEqualTo("Integration User");
        assertThat(savedUser.getEmail()).isEqualTo("integration@example.com");

        UserDto foundUser = userService.getUserById(savedUser.getId());
        assertThat(foundUser).isEqualTo(savedUser);
    }

    @Test
    void getUserById_shouldThrowExceptionIfNotFound() {
        assertThatThrownBy(() -> userService.getUserById(999))
                .isInstanceOf(NoSuchElementException.class);
    }
}
