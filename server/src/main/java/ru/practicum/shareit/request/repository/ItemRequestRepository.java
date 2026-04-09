package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Integer> {

    @Query("SELECT ir FROM ItemRequest ir WHERE ir.requesterId = ?1 ORDER BY ir.created DESC")
    List<ItemRequest> findByRequesterIdOrderByCreatedDesc(int requesterId);

    @Query("SELECT ir FROM ItemRequest ir WHERE ir.requesterId != ?1 ORDER BY ir.created DESC")
    List<ItemRequest> findAllByRequesterIdNotOrderByCreatedDesc(int requesterId);
}