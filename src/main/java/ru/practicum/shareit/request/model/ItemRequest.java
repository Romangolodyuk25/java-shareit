package ru.practicum.shareit.request.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ItemRequest {
    @NotNull
    private Long id;
    @NotNull
    private String description;
    @NotNull
    private Long requestorId; // айди пользователя , который создал запрос
    @NotNull
    @PastOrPresent
    private LocalDateTime created;
}
