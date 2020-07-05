package com.team2.laps.exception;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import com.team2.laps.payload.ApiResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class APIExceptionHandler extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatus status, WebRequest request) {
        FieldError fieldError = ex.getBindingResult().getFieldError();
        ApiResponse apiResponse = ApiResponse.builder().success(false).message(fieldError.getDefaultMessage()).build();
        return ResponseEntity.ok(apiResponse);
    }

    @ExceptionHandler
    public final ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex) {
        String message = "";
        for (ConstraintViolation violation : ex.getConstraintViolations()) {
            message += violation.getMessage() + ", ";
        }
        message = message.substring(0, message.length() - 2);
        ApiResponse apiResponse = ApiResponse.builder().success(false).message(message).build();
        return ResponseEntity.ok(apiResponse);
    }
}