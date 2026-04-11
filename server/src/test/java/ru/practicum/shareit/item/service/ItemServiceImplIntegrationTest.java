package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequestDto;
import ru.practicum.shareit.user.dto.NewUserRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplIntegrationTest {

    private final ItemService itemService;
    private final UserService userService;

    @Test
    void addItem_and_getAllItemsFromUser() {
        NewUserRequestDto userRequest = new NewUserRequestDto();
        userRequest.setName("Owner");
        userRequest.setEmail("owner@example.com");
        UserDto owner = userService.addUser(userRequest);

        NewItemRequestDto itemRequest = new NewItemRequestDto();
        itemRequest.setName("Item Name");
        itemRequest.setDescription("Item Description");
        itemRequest.setAvailable(Optional.of(true));

        ItemDto savedItem = itemService.addItem(owner.getId(), itemRequest);

        assertThat(savedItem.getId()).isNotZero();
        assertThat(savedItem.getName()).isEqualTo("Item Name");

        List<ItemDto> items = itemService.getAllItemsFromUser(owner.getId());
        assertThat(items).hasSize(1);
        assertThat(items.get(0).getName()).isEqualTo("Item Name");
    }
}
