package de.th_ro.sqs_verkehrsapp.domain.exception;

public class ExternalTrafficApiException extends RuntimeException {

    public ExternalTrafficApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
