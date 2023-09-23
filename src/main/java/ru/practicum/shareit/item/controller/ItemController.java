package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentDtoIn;
import ru.practicum.shareit.item.comment.service.CommentService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;
    private final CommentService commentService;

    @PostMapping
    public ItemDto createItem(@Valid @RequestBody ItemDto itemDto,
                              @RequestHeader(value = "X-Sharer-User-Id") long userId
    ) {
        return itemService.createItem(itemDto, userId);
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@Valid @RequestBody ItemDto itemDto,
                              @PathVariable long id,
                              @RequestHeader(value = "X-Sharer-User-Id") long userId
    ) {
        return itemService.updateItem(itemDto, id, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text,
                                     @RequestHeader(value = "X-Sharer-User-Id") long userId
    ) {
        //return itemService.searchItems(text, userId);
        return itemService.searchItemsWithPagination(text, userId);
    }

    @GetMapping
    public List<ItemDto> getAllItems(@RequestHeader(value = "X-Sharer-User-Id") long userId) {
        //return itemService.getAllItem(userId);
        return itemService.getAllItemWithPagination(userId);
    }

    @GetMapping("/{id}")
    public ItemDto getItemById(@PathVariable long id,
                               @RequestHeader(value = "X-Sharer-User-Id") long userId
    ) {
        return itemService.getItemById(id, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@PathVariable long itemId,
                                    @RequestBody CommentDtoIn commentDtoIn,
                                    @RequestHeader(value = "X-Sharer-User-Id") long userId
    ) {
        return commentService.createComment(itemId, commentDtoIn, userId);
    }

}
