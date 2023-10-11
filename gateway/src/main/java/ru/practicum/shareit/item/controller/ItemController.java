package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDtoIn;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/items")
public class ItemController {

    private final ItemClient itemClient;
    private final CommentClient commentClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@Valid @RequestBody ItemDto itemDto,
                                             @RequestHeader(value = "X-Sharer-User-Id") long userId) {
        log.info("Create item itemDto {}", itemDto);
        return itemClient.createItem(itemDto, userId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(@Valid @RequestBody ItemDto itemDto,
                                                    @PathVariable long id,
                                                    @RequestHeader(value = "X-Sharer-User-Id") long userId) {
        log.info("Update item itemDto {}", itemDto);
        return itemClient.updateItem(itemDto, id, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text,
                                     @RequestHeader(value = "X-Sharer-User-Id") long userId,
                                     @PositiveOrZero @RequestParam(required = false, defaultValue = "0") Integer from,
                                     @Positive @RequestParam(required = false, defaultValue = "10")  Integer size) {
        log.info("Search items text {}", text);
        return itemClient.searchItems(text, userId, from, size);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItems(@RequestHeader(value = "X-Sharer-User-Id") long userId,
                                     @PositiveOrZero @RequestParam(required = false, defaultValue = "0") Integer from,
                                     @Positive @RequestParam(required = false, defaultValue = "10")  Integer size) {
        log.info("Get All items userId {}", userId);
        return itemClient.getAllItems(userId, from, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItemById(@PathVariable long id,
                               @RequestHeader(value = "X-Sharer-User-Id") long userId) {
        log.info("Get item by id {}", id);
        return itemClient.getItemById(id, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@PathVariable long itemId,
                                                @RequestBody CommentDtoIn commentDtoIn,
                                                @RequestHeader(value = "X-Sharer-User-Id") long userId) {
        log.info("Create comment commentDtoIn {}", commentDtoIn);
        return commentClient.createComment(itemId, commentDtoIn, userId);
    }

}
