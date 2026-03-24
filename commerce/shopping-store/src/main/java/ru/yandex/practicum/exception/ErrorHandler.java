package ru.yandex.practicum.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleProductNotFound(final ProductNotFoundException e) {
        log.warn("ProductNotFoundException exception: {}", e.getMessage());
        return new ErrorResponse(
                e.getCause(),
                e.getStackTrace(),
                HttpStatus.NOT_FOUND,
                e.getMessage(),
                "Not Found",
                e.getSuppressed(),
                e.getLocalizedMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(final IllegalArgumentException e) {
        log.warn("IllegalArgumentException exception: {}", e.getMessage());
        return new ErrorResponse(
                e.getCause(),
                e.getStackTrace(),
                HttpStatus.BAD_REQUEST,
                e.getMessage(),
                "Illegal Argument",
                e.getSuppressed(),
                e.getLocalizedMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.warn("MethodArgumentNotValidException exception: {}", e.getMessage());
        return new ErrorResponse(
                e.getCause(),
                e.getStackTrace(),
                HttpStatus.BAD_REQUEST,
                e.getMessage(),
                "Bad Request",
                e.getSuppressed(),
                e.getLocalizedMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolationException(final ConstraintViolationException e) {
        log.warn("ConstraintViolationException exception: {}", e.getMessage());
        return new ErrorResponse(
                e.getCause(),
                e.getStackTrace(),
                HttpStatus.BAD_REQUEST,
                e.getMessage(),
                "Bad Request",
                e.getSuppressed(),
                e.getLocalizedMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInternalServerError(final Exception e) {
        log.error("Unexpected error: {}", e.getMessage());
        return new ErrorResponse(
                e.getCause(),
                e.getStackTrace(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                e.getMessage(),
                "Internal Server Error",
                e.getSuppressed(),
                e.getLocalizedMessage()
        );
    }
}
