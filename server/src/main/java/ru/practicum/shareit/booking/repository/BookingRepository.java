package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findByBooker_Id(Long bookerId, Pageable pageable);

    @Query("select b " +
            "from Booking b " +
            "where b.booker.id = ?1 AND b.start < ?2 AND b.end > ?2 ")
    Page<Booking> findAllBookingsForStateCurrent(long userId,  LocalDateTime time, Pageable pageable);

    Page<Booking> findByBooker_IdAndEndIsBefore(Long bookerId, LocalDateTime time, Pageable pageable);

    Page<Booking> findByBooker_IdAndStartIsAfter(Long bookerId, LocalDateTime time, Pageable pageable);

    Page<Booking> findByBooker_IdAndStatus(Long bookerId, Status status, Pageable pageable);

    //для владельца вещей

    Page<Booking> findByItem_Owner_Id(Long ownerId, Pageable pageable);

    @Query("select b " +
            "from Booking as b " +
            "where b.item.owner.id = ?1 " +
            "AND b.start < ?2 AND b.end > ?2 " +
            "order by b.start desc ")
    Page<Booking> findAllBookingsForStateCurrentForOwner(long userId, LocalDateTime time, Pageable pageable);

    Page<Booking>  findByItem_Owner_IdAndEndIsBefore(Long ownerId, LocalDateTime time, Pageable pageable);

    Page<Booking> findByItem_Owner_IdAndStartIsAfter(Long ownerId, LocalDateTime time, Pageable pageable);

    Page<Booking> findByItem_Owner_IdAndStatus(Long ownerId, Status status, Pageable pageable);

    @Query("select b " +
            "from Booking b " +
            "where b.item.id = ?1 " +
            "and b.start < ?2 and b.status = 'APPROVED' " +
            "order by b.start desc ")
    List<Booking> findAllBookingByItemIdForLastBooking(long itemId, LocalDateTime time);

    @Query("select b " +
            "from Booking b " +
            "where b.item.id = ?1 " +
            "and b.start > ?2 " +
            "order by b.start asc ")
    List<Booking> findAllBookingByItemIdForNextBooking(long itemId, LocalDateTime time);

    @Query("select b " +
            "from Booking b " +
            "where b.booker.id = ?1 ")
    List<Booking> findAllBookingByUserId(long userId);
}
