package net.max.live.com.util.exception;

import lombok.extern.slf4j.Slf4j;
import net.max.live.com.helper.GenericResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
public class GenericErrorHandlerUtil {
    public static Mono<ServerResponse> buildGenericErrorResponse(Throwable e, ServerRequest serverRequest) {

        HttpStatus status;
        Map<String, Object> data = null;


        if(e instanceof ExceptionHandlerUtil)  {
            status = ((ExceptionHandlerUtil) e).code;
        }
        else{
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }


        String errorMessage = e.getMessage();
        String path = serverRequest.path();
        String requestId = serverRequest.exchange().getRequest().getId(); // Example logic for request ID
        String traceId = "Trace-ID"; // Implement logic to generate/fetch trace ID

        GenericResponseDto errorResponse = GenericResponseDto.builder()
                .message(errorMessage)
                .error(true)
                .build();

        log.info("error response built : {}", errorResponse);

        return ServerResponse
                .status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(errorResponse);
    }
}
