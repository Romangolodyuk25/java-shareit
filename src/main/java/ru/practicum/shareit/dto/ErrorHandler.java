package ru.practicum.shareit.dto;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.practicum.shareit.exception.UserNotExistObject;
import ru.practicum.shareit.exception.ItemNotExistException;
import ru.practicum.shareit.exception.NotOwnerException;

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
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(final Throwable e) {
        log.error("Ошибка", e);
        return new ErrorResponse("Ошибка", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationArgument(final MethodArgumentNotValidException e) {
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
    public ErrorResponse handleNotOwner(final NotOwnerException e) {
        return new ErrorResponse("Юзер не является владельцем вещи", e.getMessage());
    }
}
