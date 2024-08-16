package net.celloscope.com.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.celloscope.com.enums.AttributeNames;
import net.celloscope.com.filter.helper.decorator.RequestBodyDecorator;
import net.celloscope.com.filter.helper.model.TokenPayload;
import net.celloscope.com.enums.BodyAttributeNames;
import net.celloscope.com.enums.HeaderNames;
import net.celloscope.com.filter.helper.util.JwtTokenExtractorUtil;
import net.celloscope.com.filter.helper.util.Message;
import org.apache.http.entity.ContentType;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyRequestBodyGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class RequestModifyFilter extends AbstractGatewayFilterFactory<RequestModifyFilter.Config> {

    private final ObjectMapper objectMapper;

    public RequestModifyFilter() {
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
                                        JwtTokenExtractorUtil.getJwtPayload(rewriteExchange.getRequest(), objectMapper), rewriteExchange.getRequest());
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
            body.get(AttributeNames.HEADER.getValue()).put(BodyAttributeNames.Client_IP_Address.getValue(), getIpAddressFromRequest(serverHttpRequest));
        } catch (Exception exception) {
            log.error(Message.FILED_TO_SET_CLIENT_ID_IP);
            tokenPayload.addError(Message.FILED_TO_SET_CLIENT_ID_IP, HttpStatus.BAD_GATEWAY);
        }
        log.info(":::::::::PAYLOAD::::::::" + tokenPayload);
        log.info(":::::::::BODY::::::::" + body);
        return body;
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

    private String getIpAddressFromRequest(ServerHttpRequest request) {
        InetSocketAddress inetSocketAddress =  request.getRemoteAddress();
        if (inetSocketAddress != null) {
            InetAddress inetAddress = inetSocketAddress.getAddress();
            if (inetAddress != null)
                return inetAddress.getHostAddress();
        }
        return null;
    }

}

