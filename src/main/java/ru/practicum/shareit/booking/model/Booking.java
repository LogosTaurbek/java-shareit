package ru.practicum.shareit.booking.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "bookings", schema = "public")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "start_date")
    private LocalDateTime start;
    @Column(name = "end_date")
    private LocalDateTime end;
    @Column(name = "item_id")
    private Integer item;
    @Column(name = "booker_id")
    private Integer booker;
    @Column
    @Enumerated(EnumType.STRING)
    private BookingStatus status;
}
