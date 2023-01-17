package com.tui.controller;

import com.tui.exception.ClientException;
import com.tui.exception.ProblemResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

/**
 * ControllerAdvice responsible for handling exceptions
 *
 * @author denysburda
 */
@RestControllerAdvice
@Slf4j
public class ExceptionControllerAdvice {

    @ExceptionHandler(ClientException.class)
    public ResponseEntity<ProblemResponse> handleClientException(ClientException e) {
        log.error("Handling ClientException: {}", e.getMessage());
        ProblemResponse problemResponse = new ProblemResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());

        return new ResponseEntity<>(problemResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ProblemResponse> handleHttpClientErrorException(HttpClientErrorException e) {
        log.error("Handling HttpClientErrorException: {}", e.getMessage());
        ProblemResponse problemResponse = new ProblemResponse(e.getStatusCode().value(), e.getMessage());

        return new ResponseEntity<>(problemResponse, e.getStatusCode());
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<ProblemResponse> handleHttpMediaTypeNotAcceptableException(
            HttpMediaTypeNotAcceptableException e) {
        log.error("Handling HttpMediaTypeNotAcceptableException: {}", e.getMessage());
        ProblemResponse problemResponse = new ProblemResponse(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(problemResponse);
    }
}
