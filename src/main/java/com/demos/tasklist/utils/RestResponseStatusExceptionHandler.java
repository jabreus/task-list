package com.demos.tasklist.utils;

import com.demos.tasklist.tasks.exceptions.TaskNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class RestResponseStatusExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(value = {TaskNotFoundException.class})
  protected ResponseEntity<Object> notFound(Exception e, WebRequest request) {
    var bodyOfResponse = e.getMessage();
    return handleExceptionInternal(
        e, bodyOfResponse, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
  }

  @Override
  @SuppressWarnings("all")
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult()
        .getAllErrors()
        .forEach(
            (error) -> {
              String fieldName = ((FieldError) error).getField();
              String errorMessage = error.getDefaultMessage();
              errors.put(fieldName, errorMessage);
            });
    return ResponseEntity.badRequest().body(errors);
  }
}
