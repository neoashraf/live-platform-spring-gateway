package net.max.live.com.filter.webfilter;

import lombok.extern.slf4j.Slf4j;
import net.max.live.com.util.TracerUtil;
import org.slf4j.MDC;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Component
@Slf4j
public class IWebFilter implements WebFilter {

    private final TracerUtil tracerUtil;

    public IWebFilter(TracerUtil tracerUtil) {
        this.tracerUtil = tracerUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain) {
        setRequestHeaders(serverWebExchange);
        setMdcAttributeForLogBack(serverWebExchange);
        logRequest(serverWebExchange.getRequest());
        setResponseHeader(serverWebExchange);
        logResponse(serverWebExchange);
        return webFilterChain.filter(serverWebExchange);
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
