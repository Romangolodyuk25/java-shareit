package ru.practicum.shareit.request.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemRequest {

    @NotNull
    private Long id;

    @NotNull
    private String description;

    @NotNull
    private User requestor; // айди пользователя , который создал запрос

    @NotNull
    private LocalDateTime created;
}
