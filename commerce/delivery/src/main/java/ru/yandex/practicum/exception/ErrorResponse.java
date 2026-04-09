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
    private Throwable cause;
    private StackTraceElement[] stackTrace;
    private HttpStatus status;
    private String userMessage;
    private String message;
    private Throwable[] suppressed;
    private String localizedMessage;
}
