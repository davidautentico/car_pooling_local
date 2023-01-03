package com.cabify.carpooling.exceptions;

import java.util.Optional;

import com.cabify.carpooling.dtos.ProblemDetailsDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @ExceptionHandler(DuplicatedCarIdException.class)
  public ResponseEntity<Object> handleDuplicatedCarIdException(final DuplicatedCarIdException ex,
      final WebRequest request) {
    return getErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, request, ex, null);
  }

  @ExceptionHandler(DuplicatedJourneyIdException.class)
  public ResponseEntity<Object> handleDuplicatedJourneyIdException(final DuplicatedJourneyIdException ex,
      final WebRequest request) {
    return getErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, request, ex, null);
  }

  @ExceptionHandler(InvalidSeatSizeException.class)
  public ResponseEntity<Object> handleInvalidSeatSizeException(final InvalidSeatSizeException ex,
      final WebRequest request) {
    return getErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, request, ex, null);
  }

  @ExceptionHandler(JourneyNotFoundException.class)
  public ResponseEntity<Object> handleJourneyNotFoundException(final JourneyNotFoundException ex,
      final WebRequest request) {
    return getEmptyErrorResponse(HttpStatus.NOT_FOUND, request, ex, null);
  }
  @ExceptionHandler(InvalidJourneyIdException.class)
  public ResponseEntity<Object> handleInvalidJourneyIdException(final InvalidJourneyIdException ex,
      final WebRequest request) {
    return getEmptyErrorResponse(HttpStatus.BAD_REQUEST, request, ex, null);
  }

  @ExceptionHandler(CarNotAssignedException.class)
  public ResponseEntity<Object> handleJourneyNotFoundException(final InvalidJourneyIdException ex,
      final WebRequest request) {
    return getEmptyErrorResponse(HttpStatus.NO_CONTENT, request, ex, null);
  }


  @SuppressWarnings("SameParameterValue")
  private ResponseEntity<Object> getErrorResponse(final HttpStatus status,
      final WebRequest request,
      final Exception cause,
      final String message) {
    return getErrorResponse(status, getDefaultHttpHeaders(), request, cause, message);
  }

  private HttpHeaders getDefaultHttpHeaders() {
    final HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    return headers;
  }

  private void doLogErrorMessage(final WebRequest request, final Exception ex, final String message) {
    final StringBuilder messageBuilder = new StringBuilder();
    final Optional<String> path = Optional.ofNullable(request != null ? request.getContextPath() : null);
    path.ifPresent(value -> messageBuilder.append(String.format("Error in request with path: '%s': ", value)));
    if (message != null) {
      messageBuilder.append(String.format("Error message: '%s': ", message));
    }

    logger.error(messageBuilder.toString(), ex);
  }
  private ResponseEntity<Object> getErrorResponse(final HttpStatus status,
      final HttpHeaders headers,
      final WebRequest request,
      final Exception cause,
      final String message) {
    doLogErrorMessage(request, cause, message);

    // Build and return response to caller
    final ProblemDetailsDTO errorDTO = new ProblemDetailsDTO();
    errorDTO.setType(request.getContextPath());
    errorDTO.setTitle(cause.getMessage());
    return new ResponseEntity<>(errorDTO, headers, status);
  }

  private ResponseEntity<Object> getEmptyErrorResponse(final HttpStatus status,
      final WebRequest request,
      final Exception cause,
      final String message) {
    doLogErrorMessage(request, cause, message);

    return new ResponseEntity<>(null, getDefaultHttpHeaders(), status);
  }
}
