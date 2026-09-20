package com.ak2.live.gateway.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ResponseLogFilter extends AbstractGatewayFilterFactory<ResponseLogFilter.Config> {

    private final ObjectMapper objectMapper;

    public ResponseLogFilter() {
        super(ResponseLogFilter.Config.class);
        objectMapper = new ObjectMapper();
    }

    public static class Config {
        //Put the configuration properties for your filter here
    }


    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();
            log.info("Request : {}", request);
            log.info("Response : {}", response.getHeaders());
            return chain.filter(exchange);
        };
    }

}


