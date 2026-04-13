package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    // ===== Owner queries =====

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId ORDER BY b.start DESC")
    List<Booking> findAllByOwnerId(@Param("ownerId") int ownerId);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id= :ownerId " +
            "AND b.start <= :now AND b.end >= :now ORDER BY b.start DESC")
    List<Booking> findCurrentByOwnerId(@Param("ownerId") int ownerId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id= :ownerId " +
            "AND b.end < :now ORDER BY b.start DESC")
    List<Booking> findPastByOwnerId(@Param("ownerId") int ownerId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId " +
            "AND b.start > :now ORDER BY b.start DESC")
    List<Booking> findFutureByOwnerId(@Param("ownerId") int ownerId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId " +
            "AND b.status = :status ORDER BY b.start DESC")
    List<Booking> findByOwnerIdAndStatus(@Param("ownerId") int ownerId, @Param("status") BookingStatus status);

    // ===== Booker queries =====

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId ORDER BY b.start DESC")
    List<Booking> findAllByBookerOrderByStartDesc(@Param("bookerId") int bookerId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId " +
            "AND b.start < :now AND b.end > :now ORDER BY b.start DESC")
    List<Booking> findAllByBookerAndStartBeforeAndEndAfterOrderByStartDesc(
            @Param("bookerId") int bookerId,
            @Param("now") LocalDateTime now,
            @Param("now") LocalDateTime now2  // Spring Data требует два параметра
    );

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId " +
            "AND b.end < :now ORDER BY b.start DESC")
    List<Booking> findAllByBookerAndEndBeforeOrderByStartDesc(
            @Param("bookerId") int bookerId,
            @Param("now") LocalDateTime now
    );

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId " +
            "AND b.start > :now ORDER BY b.start DESC")
    List<Booking> findAllByBookerAndStartAfterOrderByStartDesc(
            @Param("bookerId") int bookerId,
            @Param("now") LocalDateTime now
    );

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId " +
            "AND b.status = :status ORDER BY b.start DESC")
    List<Booking> findAllByBookerAndStatusOrderByStartDesc(
            @Param("bookerId") int bookerId,
            @Param("status") BookingStatus status
    );

    // ===== Item queries =====

    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId AND b.start <= :now " +
            "AND b.status = 'APPROVED' ORDER BY b.start DESC LIMIT 1")
    Booking findLastBookingForItem(@Param("itemId") int itemId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId AND b.start > :now " +
            "AND b.status = 'APPROVED' ORDER BY b.start ASC LIMIT 1")
    Booking findNextBookingForItem(@Param("itemId") int itemId, @Param("now") LocalDateTime now);

    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Booking b " +
            "WHERE b.booker.id = :bookerId AND b.item.id = :itemId " +
            "AND b.end < :now AND b.status = :status")
    boolean existsByBookerAndItemAndEndBeforeAndStatus(
            @Param("bookerId") int bookerId,
            @Param("itemId") int itemId,
            @Param("now") LocalDateTime now,
            @Param("status") BookingStatus status
    );

    @Query("SELECT b FROM Booking b WHERE b.item IN :items AND b.status = 'APPROVED' ORDER BY b.start ASC")
    List<Booking> findAllByItemsAndStatusApproved(@Param("items") List<Item> items);
}