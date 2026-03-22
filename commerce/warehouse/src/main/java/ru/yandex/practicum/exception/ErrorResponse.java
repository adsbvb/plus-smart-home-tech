package ru.yandex.practicum.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    Throwable cause;
    StackTraceElement[] stackTrace;
    HttpStatus status;
    String userMessage;
    String message;
    Throwable[] suppressed;
    String localizedMessage;
}
