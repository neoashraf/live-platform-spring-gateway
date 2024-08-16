package net.max.live.com.filter.helper.decorator;

import net.max.live.com.enums.HeaderNames;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class RequestHeaderIpFilter implements GatewayFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest serverHttpRequest = (ServerHttpRequest) exchange.getRequest();
//        serverHttpRequest.getHeaders().add("Source-IP-Address", getSourceIpAddress(serverHttpRequest));
        serverHttpRequest.getHeaders().add(HeaderNames.Client_IP_Address.getValue(), serverHttpRequest.getRemoteAddress().getAddress().getHostAddress());
        return chain.filter(exchange);
    }

    private String getSourceIpAddress(ServerHttpRequest serverHttpRequest) {
        StringBuilder ipStringBuilder = new StringBuilder();
        for (String header : IP_HEADER_CANDIDATES) {
            List<String> list = serverHttpRequest.getHeaders().get(header);
            if (list != null && ! list.isEmpty()) {
                for (String ip : list) {
                    if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
                        ipStringBuilder.append(ip).append(" ");
                    }
                }
            }
        }
        return ipStringBuilder.toString();
    }
    private static final String[] IP_HEADER_CANDIDATES = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR" };
}
