package com.course.devops.white.exception.handler;

import java.util.stream.Collectors;

import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.course.devops.white.api.response.base.JsonBaseError;
import com.course.devops.white.api.response.base.JsonBaseResponse;
import com.course.devops.white.constant.ApiGenericExceptionHandlerConstants;

import feign.FeignException.FeignClientException;

/**
 * Generic exception handler for <code>RestController</code>.
 * 
 * @author timpamungkas
 *
 */
@RestControllerAdvice
public class ApiGenericExceptionHandler {

  @ExceptionHandler({ ConstraintViolationException.class })
  public ResponseEntity<JsonBaseResponse<String>> handleConstraintViolationException(ConstraintViolationException e) {
    var startTime = System.currentTimeMillis();

    var message = e.getConstraintViolations().stream()
        .map(v -> v.getPropertyPath() + " (current value " + v.getInvalidValue() + ") " + v.getMessage())
        .collect(Collectors.joining(", "));

    var genericError = JsonBaseError.builder().code(ApiGenericExceptionHandlerConstants.CODE_BAD_REQUEST)
        .message(ApiGenericExceptionHandlerConstants.MESSAGE_BAD_INPUT_FROM_CLIENT).reason(message).build();
    var body = new JsonBaseResponse<String>(startTime, genericError);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).body(body);
  }

  @ExceptionHandler({ FeignClientException.class })
  public ResponseEntity<JsonBaseResponse<String>> handleFeignClientException(FeignClientException e) {
    var startTime = System.currentTimeMillis();

    var genericError = JsonBaseError.builder().code(ApiGenericExceptionHandlerConstants.CODE_API_CHAIN_CALL_ERROR)
        .message(ApiGenericExceptionHandlerConstants.MESSAGE_API_CHAIN_CALL_ERROR).reason(e.getMessage()).build();
    var body = new JsonBaseResponse<String>(startTime, genericError);

    return ResponseEntity.status(e.status()).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .body(body);
  }

  /**
   * Handle validation exception thrown by request body invalid.
   * 
   * @param e the exception
   * @return generic-format JSON error message
   */
  @ExceptionHandler({ HttpMessageNotReadableException.class })
  public ResponseEntity<JsonBaseResponse<String>> handleHttpMessageNotReadableException(
      HttpMessageNotReadableException e) {
    var startTime = System.currentTimeMillis();

    var genericError = JsonBaseError.builder().code(ApiGenericExceptionHandlerConstants.CODE_UNREADABLE_REQUEST_BODY)
        .message(ApiGenericExceptionHandlerConstants.MESSAGE_CANNOT_READ_REQUEST)
        .reason(ApiGenericExceptionHandlerConstants.REASON_CANNOT_READ_REQUEST_BODY).build();
    var body = new JsonBaseResponse<String>(startTime, genericError);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).body(body);
  }

  /**
   * Handle validation exception thrown by <code>@Valid</code> failures.
   * 
   * @param e the exception
   * @return generic-format JSON error message
   */
  @ExceptionHandler({ org.springframework.web.bind.MethodArgumentNotValidException.class })
  public ResponseEntity<JsonBaseResponse<String>> handleMethodArgumentNotValidException(
      org.springframework.web.bind.MethodArgumentNotValidException e) {
    var startTime = System.currentTimeMillis();
    var message = e.getFieldErrors().stream()
        .map(f -> f.getField() + " (value : " + f.getRejectedValue() + ") " + f.getDefaultMessage())
        .collect(Collectors.joining(", "));

    var genericError = JsonBaseError.builder().code(ApiGenericExceptionHandlerConstants.CODE_BAD_REQUEST)
        .message(ApiGenericExceptionHandlerConstants.MESSAGE_BAD_INPUT_FROM_CLIENT).reason(message).build();
    var body = new JsonBaseResponse<String>(startTime, genericError);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).body(body);
  }

}
