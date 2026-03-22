package ru.yandex.practicum.exception;

import feign.FeignException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleProductNotFoundInWarehouse(final NoSpecifiedProductInWarehouseException e) {
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
    public ErrorResponse handleLowQuantityInWarehouse(final ProductInShoppingCartLowQuantityInWarehouse e) {
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
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleProductNotFound(final ProductNotFoundException e) {
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
    public ErrorResponse handleSpecifiedProductAlreadyInWarehouseException(final SpecifiedProductAlreadyInWarehouseException e) {
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
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ErrorResponse handleFeignException(final FeignException e) {
        return new ErrorResponse(
                e.getCause(),
                e.getStackTrace(),
                HttpStatus.SERVICE_UNAVAILABLE,
                e.getMessage(),
                "Service Unavailable",
                e.getSuppressed(),
                e.getLocalizedMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(final IllegalArgumentException e) {
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
