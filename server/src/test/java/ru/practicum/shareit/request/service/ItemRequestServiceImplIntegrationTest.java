package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.user.dto.NewUserRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplIntegrationTest {

    private final ItemRequestService itemRequestService;
    private final UserService userService;

    @Test
    void addItemRequest_and_getRequestsOfUser() {
        NewUserRequestDto userRequest = new NewUserRequestDto();
        userRequest.setName("Requester");
        userRequest.setEmail("requester@example.com");
        UserDto requester = userService.addUser(userRequest);

        NewItemRequestDto requestDto = new NewItemRequestDto();
        requestDto.setDescription("I need a drill");

        ItemRequestDto savedRequest = itemRequestService.addItemRequest(requester.getId(), requestDto);

        assertThat(savedRequest.getId()).isNotZero();
        assertThat(savedRequest.getDescription()).isEqualTo("I need a drill");

        List<ItemRequestDto> requests = itemRequestService.getRequestsOfUser(requester.getId());
        assertThat(requests).hasSize(1);
        assertThat(requests.get(0).getDescription()).isEqualTo("I need a drill");
    }
}
