package com.budget_tracker.tracker.budget_tracker.exception;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;

import javax.naming.AuthenticationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.budget_tracker.tracker.budget_tracker.exception.common.BadRequestException;
import com.budget_tracker.tracker.budget_tracker.exception.common.ConflictException;
import com.budget_tracker.tracker.budget_tracker.exception.common.NotFoundException;
import com.budget_tracker.tracker.budget_tracker.exception.dto.CustomErrorResponse;

// Handles exceptions globally
@ControllerAdvice
public class GlobalExceptionHandler {

    // Handle authentication exceptions
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<CustomErrorResponse> handleAuthenticationException(AuthenticationException ex, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        CustomErrorResponse errorResponse = CustomErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(ex.getMessage()) // Set the custom message
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<CustomErrorResponse> handleBadRequest(BadRequestException ex,
            WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        CustomErrorResponse errorResponse = CustomErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(ex.getMessage()) // Set the custom message
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<CustomErrorResponse> handleConflict(ConflictException ex, WebRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        CustomErrorResponse errorResponse = CustomErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(ex.getMessage()) // Set the custom message
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<CustomErrorResponse> handleNotFound(NotFoundException ex, WebRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        CustomErrorResponse errorResponse = CustomErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(ex.getMessage()) // Set the custom message
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return ResponseEntity.status(status).body(errorResponse);
    }

    // handle 403 
    @ExceptionHandler(value = {AccessDeniedException.class})
    public ResponseEntity<CustomErrorResponse> handleAccessDenied(AccessDeniedException ex, WebRequest request) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        System.out.println("Access Denied: " + ex.getMessage());

        CustomErrorResponse errorResponse = CustomErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(ex.getMessage()) // Set the custom message
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return ResponseEntity.status(status).body(errorResponse);
    }
}
