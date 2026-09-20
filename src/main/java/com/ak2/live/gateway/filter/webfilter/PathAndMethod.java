package com.ak2.live.gateway.filter.webfilter;

import org.springframework.http.HttpMethod;

public record PathAndMethod(String path, HttpMethod method) {
    public PathAndMethod(String path, HttpMethod method) {
        this.path = path;
        this.method = method;
    }
}
