package ru.practicum.shareit.item.comment.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.comment.model.Comment;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentDtoMapper {
    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated()
                );
    }

}
