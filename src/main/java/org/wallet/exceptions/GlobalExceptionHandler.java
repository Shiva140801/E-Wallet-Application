package org.wallet.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.wallet.users.model.ApiResponse;
import org.wallet.users.model.Status;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private ObjectMapper objectMapper;

    @ExceptionHandler(ResourceNotFoundException.class)
    public void handleResourceNotFoundException(HttpServletResponse response, ResourceNotFoundException ex) throws IOException {
        writeErrorResponse(response, HttpStatus.NOT_FOUND, ex.getMessage(), Status.FAILED, null);
    }

    @ExceptionHandler(BadRequestException.class)
    public void handleBadRequestException(HttpServletResponse response, BadRequestException ex) throws IOException {
        writeErrorResponse(response, HttpStatus.BAD_REQUEST, ex.getMessage(), Status.FAILED, null);
    }

    @ExceptionHandler(ConflictException.class)
    public void handleConflictException(HttpServletResponse response, ConflictException ex) throws IOException {
        writeErrorResponse(response, HttpStatus.CONFLICT, ex.getMessage(), Status.FAILED, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public void handleValidationException(HttpServletResponse response, MethodArgumentNotValidException ex) throws IOException {

        // Safely extract field errors, even if nulls exist
        Map<String, String> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .filter(err -> err != null)
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> {
                            String message = error.getDefaultMessage();
                            return (message != null) ? message : "Invalid value";
                        },
                        (existing, replacement) -> existing
                ));


        if (fieldErrors.isEmpty()) {
            fieldErrors = Map.of("error", "Invalid request payload or missing required fields");
        }

        ApiResponse<Map<String, String>> errorResponse = new ApiResponse<>(
                Status.ERROR,
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                fieldErrors
        );

        writeResponse(response, HttpStatus.BAD_REQUEST, errorResponse);
    }


    @ExceptionHandler(Exception.class)
    public void handleGenericException(HttpServletResponse response, Exception ex) throws IOException {
        ApiResponse<String> errorResponse = new ApiResponse<>(
                Status.ERROR,
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Unexpected error: " + ex.getMessage(),
                null
        );
        writeResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, errorResponse);
    }

    private void writeResponse(HttpServletResponse response, HttpStatus status, Object body) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }

    private <T> void writeErrorResponse(HttpServletResponse response, HttpStatus httpStatus, String message, Status status, T data)
            throws IOException {

        ApiResponse<T> errorResponse = new ApiResponse<>(
                status,
                httpStatus.value(),
                message,
                data
        );

        response.setStatus(httpStatus.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
