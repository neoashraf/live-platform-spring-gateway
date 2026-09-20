/*
package com.ak2.live.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import enums.com.ak2.live.gateway.AttributeNames;
import enums.com.ak2.live.gateway.BodyAttributeNames;
import model.helper.filter.com.ak2.live.gateway.TokenPayload;
import util.helper.filter.com.ak2.live.gateway.JwtTokenExtractorUtil;
import util.helper.filter.com.ak2.live.gateway.Message;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyRequestBodyGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class QueryParamModifyFilter extends AbstractGatewayFilterFactory<QueryParamModifyFilter.Config> {

    private final ObjectMapper objectMapper;

    public QueryParamModifyFilter() {
        super(Config.class);
        objectMapper = new ObjectMapper();
    }

    public static class Config {
        //Put the configuration properties for your filter here
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ModifyRequestBodyGatewayFilterFactory.Config modifyRequestConfig = new ModifyRequestBodyGatewayFilterFactory.Config()
                    .setRewriteFunction(String.class, Map.class,
                            (rewriteExchange, originalRequestBody) -> {
                                Map<String, Map<String, String>> modifiedRequestBody = modifyRequestBody(originalRequestBody,
                                        "AUTHORIZED_LOGIN_ID", rewriteExchange.getRequest());
                                return Mono.just(modifiedRequestBody);
                            }
                    );
            return new ModifyRequestBodyGatewayFilterFactory().apply(modifyRequestConfig).filter(exchange, chain);
        };
    }

    private Map<String, Map<String, String>> modifyRequestBody(String originalRequestString, TokenPayload tokenPayload, ServerHttpRequest serverHttpRequest) {
        Map<String, Map<String, String>> body = getRequestAsMap(originalRequestString);
        try {
            body.get(AttributeNames.HEADER.getValue()).put(BodyAttributeNames.Client_ID.getValue(), tokenPayload.getClientId());
//            body.get(AttributeNames.HEADER.getValue()).put(BodyAttributeNames.Client_IP_Address.getValue(), "192.168.190.12");
//            body.get(AttributeNames.HEADER.getValue()).put(BodyAttributeNames.Client_IP_Address.getValue(), getIpAddressFromRequest(serverHttpRequest));
        } catch (Exception exception) {
            log.error(Message.FILED_TO_SET_CLIENT_ID_IP);
            tokenPayload.addError(Message.FILED_TO_SET_CLIENT_ID_IP, HttpStatus.BAD_GATEWAY);
        }
        log.info(":::::::::PAYLOAD::::::::" + tokenPayload);
        log.info(":::::::::BODY::::::::" + body);
        return body;
    }


    private void modifyQueryParam(ServerHttpRequest serverHttpRequest, String loginId) {

    }

    private HashMap<String, Map<String, String>> getRequestAsMap(String originalRequestString) {
        HashMap<String, Map<String, String>> body;
        try {
            body = objectMapper.readValue(originalRequestString, HashMap.class);
        } catch (JsonProcessingException e) {
            log.error("failed to map original request :" + originalRequestString);
            return new HashMap<>();
        }
        return body;
    }

    */
/*private String getIpAddressFromRequest(ServerHttpRequest request) {
        InetSocketAddress inetSocketAddress =  request.getRemoteAddress();
        if (inetSocketAddress != null) {
            InetAddress inetAddress = inetSocketAddress.getAddress();
            if (inetAddress != null)
                return inetAddress.getHostAddress();
        }
        return null;
    }*//*


}

*/
