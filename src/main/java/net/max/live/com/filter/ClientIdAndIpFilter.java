package net.max.live.com.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.max.live.com.filter.helper.model.TokenPayload;
import net.max.live.com.enums.HeaderNames;
import net.max.live.com.filter.helper.util.JwtTokenExtractorUtil;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

@Slf4j
@Component
public class ClientIdAndIpFilter extends AbstractGatewayFilterFactory<ClientIdAndIpFilter.Config> {

    private final ObjectMapper objectMapper;

    public ClientIdAndIpFilter() {
        super(ClientIdAndIpFilter.Config.class);
        objectMapper = new ObjectMapper();
    }

    public static class Config {
        //Put the configuration properties for your filter here
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            if (request.getHeaders().containsKey(HeaderNames.Authorization.getValue())) {
                TokenPayload tokenPayload = JwtTokenExtractorUtil.getJwtPayload(request, objectMapper);
                if (!tokenPayload.hasError()) {
                    ServerWebExchange modifiedExchange = modifyRequestHeader(exchange, tokenPayload);
                    log.info("Request Headers " + modifiedExchange.getRequest().getHeaders());
                    return chain.filter(modifiedExchange);
                }
            }
            return chain.filter(exchange);
        };
    }

    private ServerWebExchange modifyRequestHeader(ServerWebExchange originalExchange, TokenPayload tokenPayload) {
        return originalExchange.mutate()
                .request(originalRequest -> originalRequest
                        .headers(headers -> {
                            headers.set(HeaderNames.Client_ID.getValue(), tokenPayload.getClientId());
                            headers.set(HeaderNames.Client_IP_Address.getValue(), tokenPayload.getClientAddress());
                        })).build();
    }

}

