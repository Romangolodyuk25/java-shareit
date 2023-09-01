package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Entity
@Table(name = "BOOKINGS")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id", nullable = false)
    private Long id;

    @PastOrPresent
    @Column(name = "start_date", nullable = false)
    private LocalDateTime start;

    @PastOrPresent
    @Column(name = "end_date", nullable = false)
    private LocalDateTime end;

    @NotNull
    @OneToOne//не уверен
    private Item item;

    @NotNull
    @OneToOne//не уверен
    //указать название столбца
    private User booker;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    private Status status;
}
