package ru.practicum.shareit.item.comment.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Comment")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @Column(name = "text")
    private String text;

    @JoinColumn(name = "item_id")
    //аннотация которая нужна для сущности которая не имеет жизненный цикл
    private Item item;

    @JoinColumn(name = "author_id")
    @ManyToOne
    private User author;
}
