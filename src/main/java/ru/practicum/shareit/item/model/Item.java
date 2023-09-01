package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.internal.util.stereotypes.Lazy;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;

@Data
@AllArgsConstructor
@Entity
@Table(name = "ITEMS")
public class Item {
    @Id
    @Column(name = "item_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "available", nullable = false)
    private Boolean available;

    @Column(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User owner;
}
