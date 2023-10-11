package ru.practicum.shareit.dto;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.*;

import javax.validation.ValidationException;


@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final ValidationException e) {
        log.error("Ошибка валидации", e);
        return new ErrorResponse("Ошибка валидации", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotExist(final UserNotExistObject e) {
        log.error("Юзер не существуе", e);
        return new ErrorResponse("Юзер не существуе", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotExist(final ItemNotExistException e) {
        log.error("Вещи не существуе", e);
        return new ErrorResponse("Вещи не существуе", e.getMessage());
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleBooking(final BookingNotExistException e) {
        log.error("брони не существуе", e);
        return new ErrorResponse("Брони не существует", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleItemRequestException(final ItemRequestNotExist e) {
        log.error("Запроса не существуе", e);
        return new ErrorResponse("Запроса не существует", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotOwner(final IsNotOwnerException e) {
        log.error("Юзер не является владельце", e);
        return new ErrorResponse("Юзер не является владельцем", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleIsOwner(final UserIsOwnerException e) {
        log.error("Юзер является владельцем", e);
        return new ErrorResponse("Юзер является владельцем вещи", e.getMessage());
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIsOwnerException(final IsNotAvailableException e) {
        return new ErrorResponse("Вещь нельзя забронировать", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleStatusAlreadyException(final StatusAlreadyApprovedException e) {
        return new ErrorResponse("Статус уже подтвержден", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseUnsupp handleUnsupported(final UnsupportedStatusExist e) {
        return new ErrorResponseUnsupp("Unknown state: UNSUPPORTED_STATUS");
    }

}
