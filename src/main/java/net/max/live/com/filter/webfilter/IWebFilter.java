package net.max.live.com.filter.webfilter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jknack.handlebars.internal.lang3.tuple.Pair;
import lombok.extern.slf4j.Slf4j;
import net.max.live.com.util.DeviceValidatorUtil;
import net.max.live.com.util.TracerUtil;
import net.max.live.com.util.exception.ErrorBody;
import net.max.live.com.util.exception.ErrorResponse;
import net.max.live.com.util.exception.ExceptionHandlerUtil;
import org.slf4j.MDC;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static net.max.live.com.filter.webfilter.AllowedPaths.allowedPaths;

@Component
@Slf4j
public class IWebFilter implements WebFilter {

    private final TracerUtil tracerUtil;
    private final DeviceValidatorUtil deviceValidatorUtil;


    public IWebFilter(TracerUtil tracerUtil, DeviceValidatorUtil deviceValidatorUtil) {
        this.tracerUtil = tracerUtil;
        this.deviceValidatorUtil = deviceValidatorUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        setRequestHeaders(exchange);
        setMdcAttributeForLogBack(exchange);
        logRequest(exchange.getRequest());

        Map<String, String> capturedMdcContext = Optional.ofNullable(MDC.getCopyOfContextMap())
                .orElse(Collections.emptyMap());

        HttpMethod method = exchange.getRequest().getMethod();
        String path = exchange.getRequest().getPath().value();
        boolean isPublicApi = AllowedPaths.allowedPaths.contains(new PathAndMethod(path, method));

        Mono<Void> processingFlow = enrichRequestWithTokenAttributes(exchange)
                .then(Mono.defer(() -> {
                    if (isPublicApi) {
                        log.info("Public API detected. Skipping device validation.");
                        return proceedWithoutDeviceValidation(exchange, chain);
                    } else {
                        return runDeviceValidationWhenClientIsMobile(exchange)
                                .doOnSuccess(unused -> log.info("Device validation passed"))
                                .then(Mono.defer(() -> proceedWithoutDeviceValidation(exchange, chain)))
                                .onErrorResume(ExceptionHandlerUtil.class, ex -> handleValidationFailure(exchange, ex));
                    }
                }));

        return processingFlow.contextWrite(ctx -> ctx.put("mdcContextMap", capturedMdcContext));
    }

    private Mono<Void> enrichRequestWithTokenAttributes(ServerWebExchange exchange) {
        return exchange.getPrincipal()
                .filter(BearerTokenAuthentication.class::isInstance)
                .cast(BearerTokenAuthentication.class)
                .flatMap(user -> {
                    Map<String, String> queryParams = buildQueryParams(user);
                    ServerWebExchange modifiedExchange = modifyRequestQueryParams(exchange, queryParams);
                    exchange.mutate().request(modifiedExchange.getRequest()).build();
                    return Mono.empty();
                })
                .onErrorResume(e -> {
                    log.warn("Failed to enrich request with token attributes: {}", e.getMessage());
                    return Mono.empty();
                }).then();
    }

    private Map<String, String> buildQueryParams(BearerTokenAuthentication user) {
        Map<String, String> params = new HashMap<>();
        putIfNotBlank(params, "userName", (String) user.getTokenAttributes().get("username"));
        putIfNotBlank(params, "keycloakId", (String) user.getTokenAttributes().get("keycloakId"));
        putIfNotBlank(params, "email", (String) user.getTokenAttributes().get("email"));

        Object azp = user.getTokenAttributes().get("azp");
        if (azp != null) {
            params.put("azp", azp.toString());
        }

        log.info("Query Params built: {}", params);
        return params;
    }

    private void putIfNotBlank(Map<String, String> map, String key, String value) {
        if (value != null && !value.isBlank()) {
            map.put(key, value);
        }
    }

    private ServerWebExchange modifyRequestQueryParams(ServerWebExchange originalExchange, Map<String, String> queryParams) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUri(originalExchange.getRequest().getURI());

        queryParams.forEach((key, value) -> {
            if (value != null && !value.isBlank()) {
                uriBuilder.queryParam(key, value);
            }
        });

        ServerHttpRequest mutatedRequest = originalExchange.getRequest().mutate()
                .uri(uriBuilder.build().toUri())
                .build();

        return originalExchange.mutate().request(mutatedRequest).build();
    }

    private Mono<Void> handleValidationFailure(ServerWebExchange exchange, ExceptionHandlerUtil ex) {
        log.error("Device validation failed: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(ex, exchange.getRequest().getPath().toString());
        exchange.getResponse().setStatusCode(ex.getCode());

        byte[] bytes = serializeErrorResponse(errorResponse);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);

        return exchange.getResponse().writeWith(Mono.just(buffer));
    }


    private byte[] serializeErrorResponse(ErrorResponse errorResponse) {
        try {
            // Convert errorResponse to JSON using Jackson ObjectMapper
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsBytes(errorResponse);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize error response: {}", e.getMessage());
            return new byte[0];
        }
    }



    private Mono<Void> proceedWithoutDeviceValidation(ServerWebExchange exchange, WebFilterChain chain) {
        setResponseHeader(exchange);
        logResponse(exchange);
        return chain.filter(exchange);
    }

    private Mono<Void> runDeviceValidationWhenClientIsMobile(ServerWebExchange exchange) {
        return exchange.getPrincipal()
                .filter(principal -> principal instanceof BearerTokenAuthentication)
                .cast(BearerTokenAuthentication.class)
                .flatMap(token -> {
                    log.info("Token received for device validation");
                    log.info("Token attributes: {}", token.getTokenAttributes());
                    String azp = (String) token.getTokenAttributes().get("azp");
                    log.info("azp: {}", azp);
                    if ("max-live-web".equalsIgnoreCase(azp)) {
                        return deviceValidatorUtil.validateDeviceOrThrow(exchange);
                    }
                    return Mono.empty();
                });
    }




    private void logRequest(ServerHttpRequest request) {
        log.info("""
                        Request Received From {}
                         Uri : {}
                         Method : {}
                         Headers : {}
                         Path : {}
                         Query Params : {}
                         Content type : {}
                         Acceptable Media Type {}
                        """,
                request.getLocalAddress(),
                request.getURI(),
                request.getMethod(),
                request.getHeaders(),
                request.getPath(),
                request.getQueryParams(),
                request.getHeaders().getContentType(),
                request.getHeaders().getAccept());
    }

    private void logResponse(ServerWebExchange serverWebExchange) {
        serverWebExchange.getResponse().beforeCommit(() -> {
            log.info("""
                            Response Sending To {}
                             Uri : {}
                             Path : {}
                             Headers : {}
                             Response Status : {}
                             Content type : {}
                             Response 11 : {}
                            """,
                    serverWebExchange.getRequest().getLocalAddress(),
                    serverWebExchange.getRequest().getURI(),
                    serverWebExchange.getRequest().getPath(),
                    serverWebExchange.getResponse().getHeaders(),
                    serverWebExchange.getResponse().getStatusCode(),
                    serverWebExchange.getResponse().getHeaders().getContentType(),
                    serverWebExchange.getAttributeOrDefault("body","nothing")
            );
            log.info("Response processing time for {} is {} ms", serverWebExchange.getRequest().getPath(),
                    Objects.requireNonNull(serverWebExchange.getResponse().getHeaders().
                            get(HeaderNames.RESPONSE_PROCESSING_TIME_IN_MS.getValue())).stream().findFirst().orElse("0"));
            return Mono.empty();
        });
    }

    private void setResponseHeader(ServerWebExchange serverWebExchange) {
        serverWebExchange.getResponse().beforeCommit(() -> {
            Objects.requireNonNull(serverWebExchange.getRequest().getHeaders().get(HeaderNames.REQUEST_RECEIVED_TIME_IN_MS.getValue()))
                    .stream().
                    findFirst()
                    .ifPresent(s -> {
                        serverWebExchange.getResponse().getHeaders().set(HeaderNames.RESPONSE_PROCESSING_TIME_IN_MS.getValue(),
                                String.valueOf(
                                        LocalDateTime.now()
                                                .atZone(ZoneId.systemDefault())
                                                .toInstant()
                                                .toEpochMilli()
                                                - LocalDateTime
                                                .parse(s, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS"))
                                                .atZone(ZoneId.systemDefault())
                                                .toInstant()
                                                .toEpochMilli()
                                )
                        );
                    });
            serverWebExchange.getResponse().getHeaders().set(HeaderNames.RESPONSE_SENT_TIME_IN_MS.getValue(), String.valueOf(LocalDateTime.now()));
            serverWebExchange.getResponse().getHeaders().set(HeaderNames.TRACE_ID.getValue(),
                    Objects.requireNonNull(serverWebExchange.getRequest().getHeaders().get(HeaderNames.TRACE_ID.getValue()))
                            .stream()
                            .findAny()
                            .orElse(""));
            return Mono.empty();
        });
    }

    private void setRequestHeaders(ServerWebExchange serverWebExchange) {
        serverWebExchange.mutate().request(originalRequest -> originalRequest
                .headers(headers -> {
                    headers.set(HeaderNames.REQUEST_RECEIVED_TIME_IN_MS.getValue(), String.valueOf(LocalDateTime.now()));
                    if (Objects.isNull(headers.get(HeaderNames.TRACE_ID.getValue()))
                            || Objects.requireNonNull(headers.get(HeaderNames.TRACE_ID.getValue()))
                            .stream()
                            .allMatch(String::isEmpty))
                        headers.set(HeaderNames.TRACE_ID.getValue(), tracerUtil.getCurrentTraceId());
                })).build();
    }

    private void setMdcAttributeForLogBack(ServerWebExchange serverWebExchange) {
        MDC.put("Method", Objects.requireNonNull(serverWebExchange.getRequest().getMethod()).name());
        MDC.put("Uri", serverWebExchange.getRequest().getPath().value());
        MDC.put("Trace-Id", tracerUtil.getCurrentTraceId());
    }

}
