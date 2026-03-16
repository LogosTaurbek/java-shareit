package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.exceptions.UserNotValidException;
import ru.practicum.shareit.user.dto.NewUserRequestDto;
import ru.practicum.shareit.user.dto.UpdateUserRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private NewUserRequestDto newUserRequestDto;
    private UpdateUserRequestDto updateUserRequestDto;
    private UserDto expectedUserDto;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");

        newUserRequestDto = new NewUserRequestDto();
        newUserRequestDto.setName("New User");
        newUserRequestDto.setEmail("newuser@example.com");

        updateUserRequestDto = new UpdateUserRequestDto();
        updateUserRequestDto.setName("Updated User");
        updateUserRequestDto.setEmail("updated@example.com");

        expectedUserDto = new UserDto();
        expectedUserDto.setId(1);
        expectedUserDto.setName("Test User");
        expectedUserDto.setEmail("test@example.com");
    }

    @Test
    void addUser_ValidUserRequestDto_ReturnsUserDto() {
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserDto result = userService.addUser(newUserRequestDto);

        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getName(), result.getName());
        assertEquals(testUser.getEmail(), result.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void addUser_NullName_ThrowsUserNotValidException() {
        newUserRequestDto.setName(null);

        assertThrows(UserNotValidException.class, () -> userService.addUser(newUserRequestDto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void addUser_BlankName_ThrowsUserNotValidException() {
        newUserRequestDto.setName("   ");

        assertThrows(UserNotValidException.class, () -> userService.addUser(newUserRequestDto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void addUser_NullEmail_ThrowsUserNotValidException() {
        newUserRequestDto.setEmail(null);

        assertThrows(UserNotValidException.class, () -> userService.addUser(newUserRequestDto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void addUser_BlankEmail_ThrowsUserNotValidException() {
        newUserRequestDto.setEmail("");

        assertThrows(UserNotValidException.class, () -> userService.addUser(newUserRequestDto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void addUser_EmailWithoutAtSymbol_ThrowsUserNotValidException() {
        newUserRequestDto.setEmail("invalidemail.com");

        assertThrows(UserNotValidException.class, () -> userService.addUser(newUserRequestDto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserById_ExistingUserId_ReturnsUserDto() {
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));

        UserDto result = userService.getUserById(1);

        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getName(), result.getName());
        assertEquals(testUser.getEmail(), result.getEmail());
        verify(userRepository).findById(1);
    }

    @Test
    void getUserById_NonExistingUserId_ThrowsNoSuchElementException() {
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> userService.getUserById(999));
        verify(userRepository).findById(999);
    }

    @Test
    void validateNewUserRequestDto_ValidData_DoesNotThrowException() {
        assertDoesNotThrow(() -> userService.validateNewUserRequestDto(newUserRequestDto));
    }

    @Test
    void validateNewUserRequestDto_NullName_ThrowsUserNotValidException() {
        newUserRequestDto.setName(null);

        assertThrows(UserNotValidException.class, () -> userService.validateNewUserRequestDto(newUserRequestDto));
    }

    @Test
    void validateNewUserRequestDto_BlankName_ThrowsUserNotValidException() {
        newUserRequestDto.setName("");

        assertThrows(UserNotValidException.class, () -> userService.validateNewUserRequestDto(newUserRequestDto));
    }

    @Test
    void validateNewUserRequestDto_NullEmail_ThrowsUserNotValidException() {
        newUserRequestDto.setEmail(null);

        assertThrows(UserNotValidException.class, () -> userService.validateNewUserRequestDto(newUserRequestDto));
    }

    @Test
    void validateNewUserRequestDto_BlankEmail_ThrowsUserNotValidException() {
        newUserRequestDto.setEmail("   ");

        assertThrows(UserNotValidException.class, () -> userService.validateNewUserRequestDto(newUserRequestDto));
    }

    @Test
    void validateNewUserRequestDto_EmailWithoutAtSymbol_ThrowsUserNotValidException() {
        newUserRequestDto.setEmail("invalidemail.com");

        assertThrows(UserNotValidException.class, () -> userService.validateNewUserRequestDto(newUserRequestDto));
    }

    @Test
    void updateUser_ExistingUserId_ValidUpdateRequestDto_ReturnsUpdatedUserDto() {
        User updatedUser = new User();
        updatedUser.setId(1);
        updatedUser.setName("Updated User");
        updatedUser.setEmail("updated@example.com");

        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserDto result = userService.updateUser(1, updateUserRequestDto);

        assertNotNull(result);
        assertEquals(updatedUser.getId(), result.getId());
        assertEquals(updatedUser.getName(), result.getName());
        assertEquals(updatedUser.getEmail(), result.getEmail());
        verify(userRepository).findById(1);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_NonExistingUserId_ThrowsNoSuchElementException() {
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> userService.updateUser(999, updateUserRequestDto));
        verify(userRepository).findById(999);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_OnlyNameProvided_UpdatesOnlyName() {
        UpdateUserRequestDto nameOnlyUpdate = new UpdateUserRequestDto();
        nameOnlyUpdate.setName("Updated Name Only");

        User updatedUser = new User();
        updatedUser.setId(1);
        updatedUser.setName("Updated Name Only");
        updatedUser.setEmail("test@example.com");

        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserDto result = userService.updateUser(1, nameOnlyUpdate);

        assertNotNull(result);
        assertEquals("Updated Name Only", result.getName());
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_OnlyEmailProvided_UpdatesOnlyEmail() {
        UpdateUserRequestDto emailOnlyUpdate = new UpdateUserRequestDto();
        emailOnlyUpdate.setEmail("updatedemail@example.com");

        User updatedUser = new User();
        updatedUser.setId(1);
        updatedUser.setName("Test User");
        updatedUser.setEmail("updatedemail@example.com");

        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserDto result = userService.updateUser(1, emailOnlyUpdate);

        assertNotNull(result);
        assertEquals("Test User", result.getName());
        assertEquals("updatedemail@example.com", result.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_EmptyUpdateRequestDto_DoesNotUpdateFields() {
        UpdateUserRequestDto emptyUpdate = new UpdateUserRequestDto();

        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserDto result = userService.updateUser(1, emptyUpdate);

        assertNotNull(result);
        assertEquals(testUser.getName(), result.getName());
        assertEquals(testUser.getEmail(), result.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void deleteUser_ExistingUserId_DeletesUser() {
        doNothing().when(userRepository).deleteById(1);

        assertDoesNotThrow(() -> userService.deleteUser(1));
        verify(userRepository).deleteById(1);
    }

    @Test
    void userMapper_userToUserDto_ReturnsCorrectDto() {
        UserDto result = UserMapper.userToUserDto(testUser);

        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getName(), result.getName());
        assertEquals(testUser.getEmail(), result.getEmail());
    }

    @Test
    void userMapper_newUserRequestDtoToUser_ReturnsCorrectUser() {
        User result = UserMapper.newUserRequestDtoToUser(newUserRequestDto);

        assertNotNull(result);
        assertEquals(newUserRequestDto.getName(), result.getName());
        assertEquals(newUserRequestDto.getEmail(), result.getEmail());
        assertEquals(0, result.getId());
    }

    @Test
    void userMapper_updateUserFields_WithValidUpdateRequest_UpdatesUser() {
        User result = UserMapper.updateUserFields(testUser, updateUserRequestDto);

        assertNotNull(result);
        assertEquals(updateUserRequestDto.getName(), result.getName());
        assertEquals(updateUserRequestDto.getEmail(), result.getEmail());
        assertEquals(testUser.getId(), result.getId());
    }

    @Test
    void userMapper_updateUserFields_WithNullName_DoesNotUpdateName() {
        UpdateUserRequestDto emailOnlyUpdate = new UpdateUserRequestDto();
        emailOnlyUpdate.setEmail("newemail@example.com");

        User result = UserMapper.updateUserFields(testUser, emailOnlyUpdate);

        assertEquals(testUser.getName(), result.getName());
        assertEquals(emailOnlyUpdate.getEmail(), result.getEmail());
    }

    @Test
    void userMapper_updateUserFields_WithNullEmail_DoesNotUpdateEmail() {
        UpdateUserRequestDto nameOnlyUpdate = new UpdateUserRequestDto();
        nameOnlyUpdate.setName("New Name");

        User result = UserMapper.updateUserFields(testUser, nameOnlyUpdate);

        assertEquals(nameOnlyUpdate.getName(), result.getName());
        assertEquals(testUser.getEmail(), result.getEmail());
    }
}
