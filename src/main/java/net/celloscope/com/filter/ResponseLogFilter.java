package net.celloscope.com.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.celloscope.com.enums.HeaderNames;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;

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


