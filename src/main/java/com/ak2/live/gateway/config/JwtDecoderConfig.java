package com.ak2.live.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;

@Configuration
public class JwtDecoderConfig {

    @Bean
    public JwtDecoder jwtDecoder() {
        // Your issuer URI, same as Keycloak's realm URL
        String issuerUri = "https://sso-dev.themaxlive.com/realms/max-live";
        return JwtDecoders.fromIssuerLocation(issuerUri);
    }
}

