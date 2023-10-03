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
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentDtoIn;
import ru.practicum.shareit.item.comment.service.CommentService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.validation.ValidationException;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
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
    BookingDto bookingDto1;
    BookingDto bookingDto2;
    BookingDto bookingDto3;
    ItemDto itemDto;
    ItemDto itemDto2;
    ItemRequestDto itemRequestDto;

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

        bookingDto1 = bookingService.createBooking(BookingDtoIn.builder()
                        .itemId(itemDto.getId())
                        .start(LocalDateTime.now().plusHours(1))
                        .end(LocalDateTime.now().plusDays(1))
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
    }

    @Test
    @DisplayName("should create comment validation booking")
    void shouldNotValidationBookingCreateComment() {
        CommentDtoIn commentDtoIn = new CommentDtoIn("CommentTest");

        assertThrows(ValidationException.class, () -> commentService.createComment(itemDto.getId(), commentDtoIn, userDto2.getId()));
    }

//    @Test
//    @DisplayName("should create comment")
//    void shouldCreateComment() {
//        CommentDtoIn commentDtoIn = new CommentDtoIn("CommentTest");
//
//        CommentDto commentDto = commentService.createComment(itemDto.getId(), commentDtoIn, userDto2.getId());
//        assertThat(commentDto.getId(), notNullValue());
//        assertThat(commentDto.getText(), equalTo(commentDto.getText()));
//        assertThat(commentDto.getAuthorName(), equalTo(userDto2.getName()));
//    }
}
