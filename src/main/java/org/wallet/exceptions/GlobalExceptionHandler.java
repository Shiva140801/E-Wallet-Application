package org.wallet.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ServerWebExchange;
import org.wallet.users.model.ApiResponse;
import org.wallet.users.model.Status;

import java.io.IOException;

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
    public void handleConflictException(HttpServletResponse response, BadRequestException ex) throws IOException {
        writeErrorResponse(response, HttpStatus.CONFLICT, ex.getMessage(), Status.FAILED, null);
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

        String json = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(json);
    }
}
