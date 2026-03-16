package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "items", schema = "public")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column
    private String name;
    @Column
    private String description;
    @Column
    private boolean available;
    @Column(name = "owner_id")
    private Integer ownerId;
    @Column(name = "request_id")
    private Integer requestId;
}