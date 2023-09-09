package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    //для того кто забронировал вещь
    @Query("select b " +
            "from Booking b " +
            "where b.booker.id = ?1 " +
            "order by b.start desc ")
    List<Booking> findAllBookingsForStateAll(long userId);

    @Query("select b " +
            "from Booking b " +
            "where b.booker.id = ?1 AND b.start < ?2 AND b.end > ?2 " +
            "order by b.start asc ")
    List<Booking> findAllBookingsForStateCurrent(long userId,  LocalDateTime time);

    @Query("select b " +
            "from Booking b " +
            "where b.booker.id = ?1 AND b.end < ?2 " +
            "order by b.start desc ")
    List<Booking> findAllBookingsForStatePast(long userId,  LocalDateTime time);

    @Query("select b " +
            "from Booking b " +
            "where b.booker.id = ?1 AND b.start > ?2 " +
            "order by b.start desc ")
    List<Booking> findAllBookingsForStateFuture(Long userId,  LocalDateTime time);

    @Query("select b " +
            "from Booking b " +
            "where b.booker.id = ?1 AND b.status = 'WAITING' " +
            "order by b.start desc ")
    List<Booking> findAllBookingsForStateWaiting(long userId);

    @Query("select b " +
            "from Booking b " +
            "where b.booker.id = ?1 AND b.status = 'REJECTED' " +
            "order by b.start desc")
    List<Booking> findAllBookingsForStateReject(long userId);

    //для владельца вещей

    @Query("select b " +
            "from Booking as b " +
            "where b.item.owner.id = ?1 " +
            "order by b.start desc ")
    List<Booking> findAllBookingsForStateAllForOwner(long userId);

    @Query("select b " +
            "from Booking as b " +
            "where b.item.owner.id = ?1 " +
            "AND b.start < ?2 AND b.end > ?2 " +
            "order by b.start desc ")
    List<Booking> findAllBookingsForStateCurrentForOwner(long userId, LocalDateTime time);

    @Query("select b " +
            "from Booking as b " +
            "where b.item.owner.id = ?1 " +
            "AND b.end < ?2 " +
            "order by b.start desc ")
    List<Booking> findAllBookingsForStatePastForOwner(long userId, LocalDateTime time);

    @Query("select b " +
            "from Booking as b " +
            "where b.item.owner.id = ?1 " +
            "AND b.start > ?2 " +
            "order by b.start desc ")
    List<Booking> findAllBookingsForStateFutureForOwner(long userId, LocalDateTime time);

    @Query("select b " +
            "from Booking as b " +
            "where b.item.owner.id = ?1 " +
            "AND b.status = 'WAITING' " +
            "order by b.start desc ")
    List<Booking> findAllBookingsForStateWaitingForOwner(long userId);

    @Query("select b " +
            "from Booking as b " +
            "where b.item.owner.id = ?1 " +
            "AND b.status = 'REJECTED' " +
            "order by b.start desc ")
    List<Booking> findAllBookingsForStateRejectForOwner(long userId);

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
