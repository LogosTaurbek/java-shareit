package ru.practicum.shareit.booking.controller;

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
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingClient bookingClient;

    private BookItemRequestDto bookItemRequestDto;

    @BeforeEach
    void setUp() {
        bookItemRequestDto = new BookItemRequestDto(1L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2));
    }

    @Test
    void bookItem_ValidData_ReturnsOk() throws Exception {
        when(bookingClient.bookItem(anyLong(), any(BookItemRequestDto.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookItemRequestDto)))
                .andExpect(status().isOk());
    }

    @Test
    void bookItem_InvalidDates_ReturnsBadRequest() throws Exception {
        BookItemRequestDto invalidDto = new BookItemRequestDto(1L, LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(1));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void approveBooking_ValidData_ReturnsOk() throws Exception {
        when(bookingClient.approveBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true"))
                .andExpect(status().isOk());
    }

    @Test
    void getBooking_ValidId_ReturnsOk() throws Exception {
        when(bookingClient.getBooking(anyLong(), anyLong()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
    }

    @Test
    void getBookings_ReturnsOk() throws Exception {
        when(bookingClient.getBookingsOfBooker(anyLong(), any(BookingState.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL"))
                .andExpect(status().isOk());
    }

    @Test
    void getBookingsOfOwner_ReturnsOk() throws Exception {
        when(bookingClient.getBookingsOfOwner(anyLong(), any(BookingState.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL"))
                .andExpect(status().isOk());
    }
}
