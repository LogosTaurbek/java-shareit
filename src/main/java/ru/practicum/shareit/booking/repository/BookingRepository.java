package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    @Query("SELECT b FROM Booking b WHERE b.item IN " +
            "(SELECT i.id FROM Item i WHERE i.ownerId = :ownerId) " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByOwnerId(@Param("ownerId") int ownerId);

    @Query("SELECT b FROM Booking b WHERE b.item IN " +
            "(SELECT i.id FROM Item i WHERE i.ownerId = :ownerId) " +
            "AND b.start <= :now AND b.end >= :now ORDER BY b.start DESC")
    List<Booking> findCurrentByOwnerId(@Param("ownerId") int ownerId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item IN " +
            "(SELECT i.id FROM Item i WHERE i.ownerId = :ownerId) " +
            "AND b.end < :now ORDER BY b.start DESC")
    List<Booking> findPastByOwnerId(@Param("ownerId") int ownerId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item IN " +
            "(SELECT i.id FROM Item i WHERE i.ownerId = :ownerId) " +
            "AND b.start > :now ORDER BY b.start DESC")
    List<Booking> findFutureByOwnerId(@Param("ownerId") int ownerId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item IN " +
            "(SELECT i.id FROM Item i WHERE i.ownerId = :ownerId) " +
            "AND b.status = :status ORDER BY b.start DESC")
    List<Booking> findByOwnerIdAndStatus(@Param("ownerId") int ownerId, @Param("status") BookingStatus status);

    List<Booking> findAllByBookerOrderByStartDesc(int bookerId);

    List<Booking> findAllByBookerAndStartBeforeAndEndAfterOrderByStartDesc(int bookerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByBookerAndEndBeforeOrderByStartDesc(int bookerId, LocalDateTime now);

    List<Booking> findAllByBookerAndStartAfterOrderByStartDesc(int bookerId, LocalDateTime now);

    List<Booking> findAllByBookerAndStatusOrderByStartDesc(int bookerId, BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.item = :itemId AND b.start <= :now " +
            "AND b.status = 'APPROVED' ORDER BY b.start DESC LIMIT 1")
    Booking findLastBookingForItem(@Param("itemId") int itemId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item = :itemId AND b.start > :now " +
            "AND b.status = 'APPROVED' ORDER BY b.start ASC LIMIT 1")
    Booking findNextBookingForItem(@Param("itemId") int itemId, @Param("now") LocalDateTime now);

    boolean existsByBookerAndItemAndEndBeforeAndStatus(int bookerId, int itemId, LocalDateTime now, BookingStatus status);
}
