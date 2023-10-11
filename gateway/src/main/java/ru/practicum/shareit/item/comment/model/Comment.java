package ru.practicum.shareit.item.comment.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Comment {

    @NotNull
    private Long id;

    @NotNull
    private String text;

    @NotNull
    private Item item;

    @NotNull
    private User author;
    private LocalDateTime created = LocalDateTime.now();
}
