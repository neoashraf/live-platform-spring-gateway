package net.celloscope.com.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.*;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;


@Configuration
@Slf4j
public class WebClientConfig {

    private final HttpClient httpClient;

//    private final HttpClient httpClientWithCert;

//    private final TracerUtil tracerUtil;


    @Value("${oauth2.baseUrl}")
    private String authUrl;


    public WebClientConfig(@Qualifier("httpClientWithTimeout") HttpClient httpClient/*, @Qualifier("secureHttpClient") HttpClient httpClientWithCert, /*TracerUtil tracerUtil*/) {
        this.httpClient = httpClient;
//        this.httpClientWithCert = httpClientWithCert;
//        this.tracerUtil = tracerUtil;
    }

    @Bean
    public WebClient authClient() {
        return getWebClient(authUrl, httpClient);
    }

    /*private WebClient getWebClient(String baseUrl) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .filter((ClientRequest request, ExchangeFunction next) -> {
//                    ClientRequest updatedRequest = setRequestHeaders(request);
                    logRequest(updatedRequest);
                    return next.exchange(updatedRequest)
                            .doOnNext((ClientResponse response) -> {
                                setResponseHeaders(updatedRequest, response);
                                logResponse(response, baseUrl);
                            });
                })
                *//*.filter(errorHandlerV2())
                .filter(getRequestLogFilter())
                .filter(getResponseLogFilter())*//*
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }*/

    private WebClient getWebClient(String baseUrl, HttpClient httpClient) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .filter((ClientRequest request, ExchangeFunction next) -> {
//                    ClientRequest updatedRequest = setRequestHeaders(request);
//                    logRequest(updatedRequest);
                    logRequest(request);
//                    return next.exchange(updatedRequest)
                    return next.exchange(request)
                            .doOnNext((ClientResponse response) -> {
//                                setResponseHeaders(updatedRequest, response);
                                logResponse(response, baseUrl);
                            });
                })
//                .filter(getRequestLogFilter())
//                .filter(getResponseLogFilter())
                .build();
    }


    /*private ClientRequest setRequestHeaders(ClientRequest request) {
        String traceId = tracerUtil.getCurrentTraceId();
        if (Optional.ofNullable(request.headers().get(HeaderNames.TRACE_ID.getValue())).isPresent()
                && !Objects.requireNonNull(request.headers().get(HeaderNames.TRACE_ID.getValue()))
                .stream()
                .allMatch(String::isEmpty))
            traceId = Objects.requireNonNull(request.headers().get(HeaderNames.TRACE_ID.getValue()))
                    .stream()
                    .findFirst()
                    .orElse(traceId);
        return ClientRequest.from(request)
                .header(HeaderNames.REQUEST_SENT_TIME_IN_MS.getValue(), String.valueOf(LocalDateTime.now()))
                .header(HeaderNames.TRACE_ID.getValue(), traceId)
                .build();
    }

    private void setResponseHeaders(ClientRequest request, ClientResponse response) {
        response.mutate().header(HeaderNames.RESPONSE_RECEIVED_TIME_IN_MS.getValue(), String.valueOf(LocalDateTime.now()));
        if (!Objects.requireNonNull(request.headers().get(HeaderNames.REQUEST_SENT_TIME_IN_MS.getValue()))
                .stream()
                .allMatch(String::isEmpty)) {
            String s = Objects.requireNonNull(request.headers().get(HeaderNames.REQUEST_SENT_TIME_IN_MS.getValue()))
                    .stream()
                    .findAny().orElse(LocalDateTime.now().toString());
            response.mutate().header(HeaderNames.RESPONSE_TRANSMISSION_TIME_IN_MS.getValue(),
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
                    ));
        }
    }*/

    private void logRequest(ClientRequest request) {
        try {
            log.info("""
                            Request Sending From {}
                             Uri : {}
                             Method : {}
                             Headers : {}
                             Content type : {}
                             Acceptable Media Type {}
                            """,
                    InetAddress.getLocalHost().getHostAddress(),
                    request.url(),
                    request.method(),
                    request.headers(),
                    request.headers().getContentType(),
                    request.headers().getAccept());
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    private void logResponse(ClientResponse response, String baseUrl) {
        log.info("""
                        Response Receiving from {}
                         Headers : {}
                         Response Status : {}
                         Content type : {}
                        """,
                baseUrl,
                response.headers().asHttpHeaders(),
                response.statusCode(),
                response.headers().contentType()
        );
//        log.info("Response receiving time from {} is {} ms", baseUrl,
//                Objects.requireNonNull(response.headers()
//                        .header(HeaderNames.RESPONSE_TRANSMISSION_TIME_IN_MS.getValue())
//                        .stream()
//                        .findFirst()
//                        .orElse("0")
//                )
//        );
    }

    /*private static ExchangeFilterFunction getResponseLogFilter() {
        LoggingProperties responseProperties = LoggingProperties.builder()
                .logRequestId(false)
                .logHeaders(true)
                .logBody(true)
                .build();


        return ClientResponseLoggingFilterFactory.defaultFilter(responseProperties);
    }decodeJwtToken

    private static ExchangeFilterFunction getRequestLogFilter() {
        LoggingProperties requestProperties = LoggingProperties.builder()
                .logRequestId(false)
                .logHeaders(true)
                .logBody(true)
                .build();

        return ClientRequestLoggingFilterFactory.defaultFilter(requestProperties);
    }*/


    /*public static ExchangeFilterFunction errorHandler() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
                    if (clientResponse.statusCode().is5xxServerError()) {
                        return clientResponse.bodyToMono(ErrorMapDto.class)
                                .flatMap(errorBody -> Mono.error(new ExceptionHandlerUtil(HttpStatus.valueOf(clientResponse.statusCode().value()), errorBody.getMessage())));
                    } else if (clientResponse.statusCode().is4xxClientError()) {
                        return clientResponse.bodyToMono(ErrorMapDto.class)
                                .flatMap(errorBody -> Mono.error(new ExceptionHandlerUtil(HttpStatus.valueOf(clientResponse.statusCode().value()), errorBody.getMessage())));
                    } else if (!clientResponse.statusCode().is2xxSuccessful()) {
                        return clientResponse.bodyToMono(ErrorMapDto.class)
                                .flatMap(errorBody -> Mono.error(new ExceptionHandlerUtil(HttpStatus.valueOf(clientResponse.statusCode().value()), errorBody.getMessage())));
                    } else {
                        return Mono.just(clientResponse);
                    }
                }
        );
    }




    public static ExchangeFilterFunction errorHandlerV2() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            HttpStatus statusCode = clientResponse.statusCode();
            MediaType contentType = clientResponse.headers().contentType().orElse(null);
            if (statusCode.is5xxServerError() || statusCode.is4xxClientError()) {
                return mapErrorResponse(clientResponse, contentType);
            } else if (!statusCode.is2xxSuccessful()) {
                return mapErrorResponse(clientResponse, contentType);
            } else {
                return Mono.just(clientResponse);
            }
        });
    }
    private static Mono<ClientResponse> mapErrorResponse(ClientResponse clientResponse, MediaType contentType) {
        Mono<Throwable> errorMono;
        if (contentType != null && contentType.isCompatibleWith(MediaType.APPLICATION_JSON)) {
            errorMono = clientResponse.bodyToMono(ErrorMapDto.class)
                    .map(errorBody -> new ExceptionHandlerUtil(clientResponse.statusCode(), errorBody.getMessage()));
        } else {
            errorMono = clientResponse.bodyToMono(String.class)
                    .map(errorBody ->{
                        log.error("unrecognized error from server while recharge request, error: {}", errorBody);
                        return new ExceptionHandlerUtil(HttpStatus.REQUEST_TIMEOUT, "service is unavailable. Please try again later");
                    });
        }
        return errorMono.flatMap(Mono::error);
    }*/



    /*public static ExchangeFilterFunction errorHandlerV3() {

        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
                    if (clientResponse.statusCode().is5xxServerError() || clientResponse.statusCode().is4xxClientError()) {
                        return mapToErrorMapDto(clientResponse);
                    } else if (!clientResponse.statusCode().is2xxSuccessful()) {
                        return mapToErrorMapDto(clientResponse);
                    } else {
                        return Mono.just(clientResponse);
                    }
                }
        );
    }
    private static Mono<ClientResponse> mapToErrorMapDto(ClientResponse clientResponse) { // TODO: ৩/৪/২৩ ei method tate somossa ache. need to fix this later
        ObjectMapper objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .registerModule(new SimpleModule().addDeserializer(ErrorMapDto.class, new ErrorMapDtoDeserializer()));
        return clientResponse.bodyToMono(String.class)
                .flatMap(body -> {
                            log.error("Error body from response:{}", body);
                            try {
                                ErrorMapDto errorBody = objectMapper.readValue(body, ErrorMapDto.class);
                                return Mono.error(new ExceptionHandlerUtil(HttpStatus.valueOf(clientResponse.statusCode().value()), errorBody.getMessage()));
                            } catch (JsonProcessingException e) {
                                log.error("Error occurred while parsing error response body , error:{}, errorBody:{}", e.getMessage(), body);
                                return Mono.error(new ExceptionHandlerUtil(HttpStatus.valueOf(clientResponse.statusCode().value()), "Unknown error occurred"));
                            }
                        }
                );
    }*/

}
