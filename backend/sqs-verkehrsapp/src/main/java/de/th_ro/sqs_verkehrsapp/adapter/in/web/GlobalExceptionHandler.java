package de.th_ro.sqs_verkehrsapp.adapter.in.web;

import de.th_ro.sqs_verkehrsapp.adapter.in.web.dto.ApiErrorResponse;
import de.th_ro.sqs_verkehrsapp.domain.exception.TrafficDataUnavailableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TrafficDataUnavailableException.class)
    public ResponseEntity<ApiErrorResponse> handleTrafficDataUnavailable(
            TrafficDataUnavailableException exception
    ) {
        ApiErrorResponse response = new ApiErrorResponse(
                "TRAFFIC_DATA_UNAVAILABLE",
                exception.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response);
    }
}
