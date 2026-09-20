package com.ak2.live.gateway.filter.webfilter;

import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;

import java.util.List;

public class CorsIWebFilter {

    /*@Bean
    public CorsWebFilter corsWebFilter() {
        // Create and configure the CORS settings
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
                "https://owner-dev.example.com",
                "https://country-admin-dev.example.com",
                "https://agency-dev.example.com",
                "https://reseller-dev.example.com",
                "https://master-portal-dev.example.com"
        ));
        config.setAllowCredentials(true);
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setMaxAge(8000L);

        // Use UrlBasedCorsConfigurationSource to register the configuration
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        // Return the CorsWebFilter with the UrlBasedCorsConfigurationSource
        return new CorsWebFilter((CorsConfigurationSource) source);
    }*/

}
