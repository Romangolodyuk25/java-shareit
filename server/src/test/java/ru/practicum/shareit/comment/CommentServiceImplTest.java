package ru.practicum.shareit.comment;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.LastBookingsNotExistException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentDtoIn;
import ru.practicum.shareit.item.comment.dto.CommentDtoMapper;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.service.CommentService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class CommentServiceImplTest {

    private final EntityManager em;

    private final ItemService itemService;
    private final UserService userService;
    private final ItemRequestService itemRequestService;
    private final BookingService bookingService;
    private final CommentService commentService;

    UserDto userDto;
    UserDto userDto2;
    BookingDto bookingDto2;
    BookingDto bookingDto3;
    ItemDto itemDto;
    ItemDto itemDto2;
    ItemRequestDto itemRequestDto;
    CommentDto commentDto;
    Comment comment;

    @BeforeEach
    void beforeEach() {
        userDto = userService.createUser(UserDto.builder()
                .name("Vanya")
                .email("vanya@email.ru")
                .build()
        );
        userDto2 = userService.createUser(UserDto.builder()
                .name("Vanya2")
                .email("vanya2@email.ru")
                .build()
        );
        itemDto = itemService.createItem(ItemDto.builder()
                .name("Пила")
                .description("Пилит")
                .available(true)
                .build(), userDto.getId());

        itemDto2 = itemService.createItem(ItemDto.builder()
                .name("Пила")
                .description("Пилит")
                .available(true)
                .build(), userDto2.getId());


        bookingDto2 = bookingService.createBooking(BookingDtoIn.builder()
                .itemId(itemDto.getId())
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusDays(1))
                .build(), userDto2.getId());

        bookingDto3 = bookingService.createBooking(BookingDtoIn.builder()
                .itemId(itemDto2.getId())
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusDays(1))
                .build(), userDto.getId());

        itemRequestDto = itemRequestService.createRequest(new ItemRequestDtoIn("что-нибудь"), userDto2.getId());

        commentDto = CommentDto.builder()
                .id(1L)
                .created(LocalDateTime.now())
                .build();

        comment = Comment.builder()
                .id(1L)
                .author(UserDtoMapper.toUser(userDto))
                .item(ItemDtoMapper.toItem(itemDto, UserDtoMapper.toUser(userDto2), new ItemRequest()))
                .text("text")
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("should not create comment validation booking")
    void shouldNotValidationBookingCreateComment() {
        CommentDtoIn commentDtoIn = new CommentDtoIn();
        commentDtoIn.setText("CommentTest");
        CommentDto commentDto = new CommentDto();
        commentDto = CommentDtoMapper.toCommentDto(Comment.builder()
                .id(commentDto.getId())
                .created(commentDto.getCreated())
                .author(UserDtoMapper.toUser(userDto))
                .text("text")
                .build());

        assertThrows(LastBookingsNotExistException.class, () -> commentService.createComment(itemDto.getId(), commentDtoIn, userDto2.getId()));
    }
}