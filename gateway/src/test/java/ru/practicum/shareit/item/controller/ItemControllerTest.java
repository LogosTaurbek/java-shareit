package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.dto.NewCommentRequestDto;
import ru.practicum.shareit.item.dto.NewItemRequestDto;
import ru.practicum.shareit.item.dto.UpdateItemRequestDto;
import ru.practicum.shareit.item.util.HttpHeaderConstants;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemClient itemClient;

    private NewItemRequestDto newItemRequestDto;
    private UpdateItemRequestDto updateItemRequestDto;
    private NewCommentRequestDto newCommentRequestDto;

    @BeforeEach
    void setUp() {
        newItemRequestDto = new NewItemRequestDto();
        newItemRequestDto.setName("Item Name");
        newItemRequestDto.setDescription("Item Description");
        newItemRequestDto.setAvailable(true);

        updateItemRequestDto = new UpdateItemRequestDto();
        updateItemRequestDto.setName("Updated Name");

        newCommentRequestDto = new NewCommentRequestDto();
        newCommentRequestDto.setText("Comment Text");
    }

    @Test
    void addItem_ValidData_ReturnsOk() throws Exception {
        when(itemClient.addItem(anyLong(), any(NewItemRequestDto.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(post("/items")
                        .header(HttpHeaderConstants.X_SHARER_USER_ID, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newItemRequestDto)))
                .andExpect(status().isOk());
    }

    @Test
    void updateItem_ValidData_ReturnsOk() throws Exception {
        when(itemClient.updateItem(anyLong(), anyInt(), any(UpdateItemRequestDto.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(patch("/items/1")
                        .header(HttpHeaderConstants.X_SHARER_USER_ID, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateItemRequestDto)))
                .andExpect(status().isOk());
    }

    @Test
    void getItemById_ValidId_ReturnsOk() throws Exception {
        when(itemClient.getItemById(anyLong(), anyInt()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(get("/items/1")
                        .header(HttpHeaderConstants.X_SHARER_USER_ID, 1))
                .andExpect(status().isOk());
    }

    @Test
    void getAllItemsFromUser_ReturnsOk() throws Exception {
        when(itemClient.getAllItemsFromUser(anyLong()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(get("/items")
                        .header(HttpHeaderConstants.X_SHARER_USER_ID, 1))
                .andExpect(status().isOk());
    }

    @Test
    void searchAvailableItems_ReturnsOk() throws Exception {
        when(itemClient.searchAvailableItems(anyLong(), anyString()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(get("/items/search")
                        .header(HttpHeaderConstants.X_SHARER_USER_ID, 1)
                        .param("text", "search"))
                .andExpect(status().isOk());
    }

    @Test
    void addComment_ValidData_ReturnsOk() throws Exception {
        when(itemClient.addComment(anyLong(), anyInt(), any(NewCommentRequestDto.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(post("/items/1/comment")
                        .header(HttpHeaderConstants.X_SHARER_USER_ID, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCommentRequestDto)))
                .andExpect(status().isOk());
    }
}
