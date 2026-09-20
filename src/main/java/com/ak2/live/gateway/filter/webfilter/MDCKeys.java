package com.ak2.live.gateway.filter.webfilter;

import lombok.Getter;

@Getter
public enum MDCKeys {
    TRACE_ID("Trace-ID"),
    METHOD("Method"),
    URI("Uri"),
    ;

    private final String value;

    MDCKeys(String value) {
        this.value = value;
    }
}
