package com.naglabs.ezquizmaster.exception;

import com.naglabs.ezquizmaster.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception ex) {
        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("code", "INTERNAL_ERROR");
        errorBody.put("message", ex.getMessage());
        errorBody.put("timestamp", Instant.now().toString());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorBody);
    }

    private ErrorResponse buildError(String code, String message, HttpServletRequest request) {
        String requestId = request.getHeader("X-Request-ID");
        return new ErrorResponse(code, message, Instant.now(), requestId != null ? requestId : "N/A");
    }

    @ExceptionHandler(LifelineAlreadyUsedException.class)
    public ResponseEntity<ErrorResponse> handleLifelineUsed(LifelineAlreadyUsedException ex, HttpServletRequest req) {
        return new ResponseEntity<>(buildError("LIFELINE_ALREADY_USED", ex.getMessage(), req), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserSessionNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleSessionNotFound(UserSessionNotFoundException ex, HttpServletRequest req) {
        return new ResponseEntity<>(buildError("SESSION_NOT_FOUND", ex.getMessage(), req), HttpStatus.NOT_FOUND);
    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex, HttpServletRequest req) {
//        return new ResponseEntity<>(buildError("INTERNAL_ERROR", "An unexpected error occurred", req), HttpStatus.INTERNAL_SERVER_ERROR);
//    }

    @ExceptionHandler(OpenAiResponseParseException.class)
    public ResponseEntity<ErrorResponse> handleParseError(OpenAiResponseParseException ex, HttpServletRequest req) {
        return new ResponseEntity<>(
                buildError("PARSE_ERROR", ex.getMessage(), req),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

}

