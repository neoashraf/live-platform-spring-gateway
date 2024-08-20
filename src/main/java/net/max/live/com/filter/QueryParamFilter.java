package net.max.live.com.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.max.live.com.enums.HeaderNames;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

@Slf4j
@Component
public class QueryParamFilter extends AbstractGatewayFilterFactory<QueryParamFilter.Config> {

    private final ObjectMapper objectMapper;

    public QueryParamFilter() {
        super(QueryParamFilter.Config.class);
        objectMapper = new ObjectMapper();
    }

    public static class Config {
        //Put the configuration properties for your filter here
    }

//    @Override
//    public GatewayFilter apply(Config config) {
//        return (exchange, chain) -> {
//            ServerHttpRequest request = exchange.getRequest();
//            if (request.getHeaders().containsKey(HeaderNames.Authorization.getValue())) {
//                ServerWebExchange modifiedExchange = modifyRequestQueryParams(exchange, "AUTHORIZED_LOGIN_ID");
//                log.info("Request Headers " + modifiedExchange.getRequest().getHeaders());
//                return chain.filter(modifiedExchange);
//            }
//            return chain.filter(exchange);
//        };
//    }
//
//    private ServerWebExchange modifyRequestQueryParams(ServerWebExchange originalExchange, String loginId) {
////        LinkedMultiValueMap<String, String> stringStringLinkedMultiValueMap = new LinkedMultiValueMap<>();
////        stringStringLinkedMultiValueMap.put("loginId", Collections.singletonList(loginId));
//        return originalExchange.mutate()
//                .request(originalRequest -> originalRequest.uri(
//                        UriComponentsBuilder.fromUri(originalExchange.getRequest()
//                                        .getURI())
//                                .queryParam("loginId", Collections.singletonList(loginId))
////                                .replaceQueryParams(stringStringLinkedMultiValueMap)
//                                .build()
//                                .toUri())).build();
//    }


    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();
            HttpHeaders headers = response.getHeaders();
            headers.remove(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);
//            headers.remove(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS);
//            headers.remove(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS);
            return exchange
//                    .getResponse().getHeaders().toSingleValueMap()
                    .getPrincipal()
                    .flatMap(principal -> {
                        // Here you can access the principal and get the user information
                        // For example, if the principal contains first name and last name:
                        log.info("Principal : {}", principal);
//                        UserDetails userName= (UserDetails) principal;
                        BearerTokenAuthentication user = (BearerTokenAuthentication) principal;
                        log.info("getTokenAttributes() : {}", user.getTokenAttributes());

                        if (request.getHeaders().containsKey(HeaderNames.Authorization.getValue())) {
                            ServerWebExchange modifiedExchange = modifyRequestQueryParams(exchange, user.getTokenAttributes().get("username").toString(),
                                    user.getTokenAttributes().get("keycloakId").toString(), user.getTokenAttributes().get("email").toString());
                            log.info("Request Headers " + modifiedExchange.getRequest().getHeaders());
                            return chain.filter(modifiedExchange);
                        }
                        return chain.filter(exchange);
//                        String firstName = principal.getAttribute("firstName");
//                        String lastName = principal.getAttribute("lastName");
//                        return "Hello, " + firstName + " " + lastName + "!";
                    });
//                    .defaultIfEmpty("Hello, anonymous!");

//            if (request.getHeaders().containsKey(HeaderNames.Authorization.getValue())) {
//                ServerWebExchange modifiedExchange = modifyRequestQueryParams(exchange, "AUTHORIZED_LOGIN_ID","MFI_ID","INSTITUTE_OID");
//                log.info("Request Headers " + modifiedExchange.getRequest().getHeaders());
//                return chain.filter(modifiedExchange);
//            }
//            return chain.filter(exchange);
        };
    }

    private ServerWebExchange modifyRequestQueryParams(ServerWebExchange originalExchange, String userName, String keycloakId, String email ) {
//        LinkedMultiValueMap<String, String> stringStringLinkedMultiValueMap = new LinkedMultiValueMap<>()
        return originalExchange.mutate()
                .request(originalRequest -> originalRequest.uri(
                        UriComponentsBuilder.fromUri(originalExchange.getRequest()
                                        .getURI())
                                .queryParam("userName", Collections.singletonList(userName))
                                .queryParam("keycloakId", Collections.singletonList(keycloakId))
                                .queryParam("email", Collections.singletonList(email))
//                                .replaceQueryParams(stringStringLinkedMultiValueMap)
                                .build()
                                .toUri())).build();

    }

}