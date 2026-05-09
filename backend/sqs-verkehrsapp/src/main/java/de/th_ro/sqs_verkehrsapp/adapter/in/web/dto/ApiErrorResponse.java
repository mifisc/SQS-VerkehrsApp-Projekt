package de.th_ro.sqs_verkehrsapp.adapter.in.web.dto;

import java.time.LocalDateTime;

public record ApiErrorResponse(String code,
                               String message,
                               LocalDateTime timestamp) {
}
