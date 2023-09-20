package ru.practicum.shareit.request.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;

@Data
@Entity
@AllArgsConstructor
@Table(name = "REQUESTS")
public class ItemRequest {
    @NotNull
    @Id
    @Column(name = "request_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String description;
    @NotNull
    @OneToOne
    private User requestor; // айди пользователя , который создал запрос
    @NotNull
    @PastOrPresent
    private LocalDateTime created;
}
