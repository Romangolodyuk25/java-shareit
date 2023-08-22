package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Booking {
    @NotNull
    private Long id;
    @NotNull
    @PastOrPresent
    private LocalDate start;
    @NotNull
    @PastOrPresent
    private LocalDate end;
    @NotNull
    private Long itemId;
    @NotNull
    private Long bookerId;
    @NotNull
    private Status status;
}
