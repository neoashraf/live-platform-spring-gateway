package com.ak2.live.gateway.notification;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.List;

public interface Notification {

    void addError(String message, HttpStatus httpStatus);
    Boolean hasError();
    List<Error> getErrors();
    Error getFirstError();

    @Data
    class Error {
        private String message;
        private HttpStatus code;
        public Error(String message) {
            this.message = message;
        }
        public Error(String message, HttpStatus code) {
            this.message = message;
            this.code = code;
        }
    }
}
