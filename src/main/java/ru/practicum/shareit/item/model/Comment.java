package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "comments", schema = "public")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column
    private String text;
    @Column(name = "item_id")
    private int item;
    @Column(name = "author_id")
    private int author;
    @Column
    private LocalDateTime created;
}
