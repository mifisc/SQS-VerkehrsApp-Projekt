package de.th_ro.sqs_verkehrsapp.domain.exception;

public class TrafficDataUnavailableException extends RuntimeException {

    public TrafficDataUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
