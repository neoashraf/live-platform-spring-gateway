package com.ak2.live.gateway.filter.helper.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import com.ak2.live.gateway.enums.HeaderNames;
import com.ak2.live.gateway.filter.helper.model.TokenPayload;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;

import java.util.Base64;

@Slf4j
public class JwtTokenExtractorUtil {

    public static TokenPayload getJwtPayload(ServerHttpRequest request, ObjectMapper objectMapper) {
        final String token = request.getHeaders().getOrEmpty(HeaderNames.Authorization.getValue()).get(0);
        String payload = decodeJwtToken(token);
        return jsonPayloadToObject(payload, objectMapper);
    }

    public static String decodeJwtToken(String token) {
        String[] chunks = token.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();
        return new String(decoder.decode(chunks[1]));
    }

    public static TokenPayload jsonPayloadToObject(String payload, ObjectMapper objectMapper) {
        TokenPayload tokenPayload;
        try {
            tokenPayload = objectMapper.readValue(payload, TokenPayload.class);
        } catch (Exception e) {
            log.error("Failed to parse string jwt payload to object");
            tokenPayload = new TokenPayload();
            tokenPayload.addError("Failed to parse jwt token", HttpStatus.BAD_GATEWAY);
        }
        return tokenPayload;
    }

}
