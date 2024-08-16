package net.celloscope.com.filter.helper.decorator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import reactor.core.publisher.Flux;

import java.util.Map;

@Slf4j
public class RequestBodyDecorator extends ServerHttpRequestDecorator {

    private final Map<String, Map<String, String>> values;
    private final ServerHttpRequest request;

    public RequestBodyDecorator(ServerHttpRequest delegate, Map<String, Map<String, String>> values) {
        super(delegate);
        this.values = values;
        this.request = delegate;
    }

    @Override
    public HttpHeaders getHeaders() {
        HttpHeaders headers = getDelegate().getHeaders();
        try {
            String newBody = new ObjectMapper().writeValueAsString(values);
            headers.setContentLength(newBody.getBytes().length);
            return headers;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return getDelegate().getHeaders();
    }

    @Override
    public Flux<DataBuffer> getBody() {

        try {
            String newBody = new ObjectMapper().writeValueAsString(values);
            DefaultDataBufferFactory factory = new DefaultDataBufferFactory();
            DataBuffer buffer = factory.wrap(newBody.getBytes());
            return Flux.just(buffer)
                    .doOnRequest(v -> log.info("I was requested"))
                    .doOnError(throwable -> log.info(""))
                    .doOnNext(dataBuffer -> log.info("Data buffer" + dataBuffer))
                    .doOnComplete(() -> log.info(""));

        } catch (JsonProcessingException e) {
            return super.getBody();
        }
    }
}
