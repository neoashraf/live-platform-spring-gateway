package com.ak2.live.gateway.util.exception;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;

@Aspect
@Component
public class ExceptionHandlingAspect {
    @Around("execution(public reactor.core.publisher.Mono<org.springframework.web.reactive.function.server.ServerResponse> *(..))")
    public Mono<ServerResponse> handleErrors(ProceedingJoinPoint joinPoint) {
        try {
            // Proceed with the original method
            Mono<ServerResponse> result = (Mono<ServerResponse>) joinPoint.proceed();

            // Apply generic error handling
            return result.onErrorResume(e -> {
                ServerRequest serverRequest = (ServerRequest) joinPoint.getArgs()[0]; // Assuming ServerRequest is the first argument
                return GenericErrorHandlerUtil.buildGenericErrorResponse(e, serverRequest);
            });

        } catch (Throwable throwable) {
            // Handle any exception that occurs during method execution
            ServerRequest serverRequest = (ServerRequest) joinPoint.getArgs()[0];
            return GenericErrorHandlerUtil.buildGenericErrorResponse(throwable, serverRequest);
        }
    }

    private void convertLocalDateTimeToUtc(Object response) {
        // Reflect over the response object and convert LocalDateTime fields to UTC ZonedDateTime
        Arrays.stream(response.getClass().getDeclaredFields())
                .filter(field -> field.getType().equals(LocalDateTime.class))
                .forEach(field -> {
                    field.setAccessible(true);
                    try {
                        LocalDateTime localDateTime = (LocalDateTime) field.get(response);
                        if (localDateTime != null) {
                            ZonedDateTime utcDateTime = localDateTime.atZone(ZoneOffset.UTC);
                            field.set(response, utcDateTime); // Replace LocalDateTime with UTC ZonedDateTime
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace(); // Handle exception if needed
                    }
                });
    }
}
