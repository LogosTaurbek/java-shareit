package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequestDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.util.HttpHeaderConstants;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @Test
    void addItem() throws Exception {
        NewItemRequestDto request = new NewItemRequestDto();
        request.setName("Item");
        request.setDescription("Description");
        request.setAvailable(Optional.of(true));

        ItemDto response = new ItemDto();
        response.setId(1);
        response.setName("Item");
        response.setDescription("Description");
        response.setAvailable(true);

        when(itemService.addItem(anyInt(), any())).thenReturn(response);

        mockMvc.perform(post("/items")
                        .header(HttpHeaderConstants.X_SHARER_USER_ID, 1)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Item"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void getItemById() throws Exception {
        ItemDto response = new ItemDto();
        response.setId(1);
        response.setName("Item");
        response.setAvailable(true);

        when(itemService.getItemById(anyInt(), anyInt())).thenReturn(response);

        mockMvc.perform(get("/items/1")
                        .header(HttpHeaderConstants.X_SHARER_USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Item"));
    }
}
