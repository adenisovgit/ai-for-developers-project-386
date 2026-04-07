package io.hexlet.callbooking.shared.error;

import jakarta.validation.ConstraintViolationException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.converter.HttpMessageNotReadableException;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<String> handleBadRequest(BadRequestException exception) {
        return plainText(400, exception.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<String> handleConflict(ConflictException exception) {
        return plainText(409, exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(FieldError::getDefaultMessage)
            .filter(value -> value != null && !value.isBlank())
            .collect(Collectors.joining("; "));

        return plainText(400, message.isBlank() ? "Некорректные данные запроса." : message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolation(ConstraintViolationException exception) {
        String message = exception.getConstraintViolations()
            .stream()
            .map(violation -> violation.getMessage())
            .filter(value -> value != null && !value.isBlank())
            .collect(Collectors.joining("; "));

        return plainText(400, message.isBlank() ? "Некорректные параметры запроса." : message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleNotReadable(HttpMessageNotReadableException exception) {
        return plainText(400, "Некорректное тело запроса.");
    }

    private ResponseEntity<String> plainText(int statusCode, String message) {
        return ResponseEntity.status(statusCode)
            .contentType(new MediaType("text", "plain", StandardCharsets.UTF_8))
            .body(message);
    }
}
