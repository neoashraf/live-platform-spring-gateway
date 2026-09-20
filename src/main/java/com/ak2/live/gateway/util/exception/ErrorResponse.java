package com.ak2.live.gateway.util.exception;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ErrorResponse implements Serializable {
    private final long timestamp = System.currentTimeMillis();
    private final int status;
    private final boolean error;
    private final String phrase;
    private final String message;
    private final String path;

    public ErrorResponse(ExceptionHandlerUtil error, String path) {
        this.status = error.getCode().value();
        this.error = true;
        this.phrase = error.getCode().toString();
        this.message = error.getMessage();
        this.path = path;
    }

    public ErrorResponse(int status, String phrase, String message, String path) {
        this.status = status;
        this.error = true;
        this.phrase = phrase;
        this.message = message;
        this.path = path;
    }
}
