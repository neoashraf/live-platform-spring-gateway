package net.max.live.com.filter.webfilter;

import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;

import java.util.List;

public class CorsIWebFilter {

    @Bean
    public CorsWebFilter corsWebFilter() {
        // Create and configure the CORS settings
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
                "https://owner-dev.themaxlive.com",
                "https://country-admin-dev.themaxlive.com",
                "https://agency-dev.themaxlive.com",
                "https://reseller-dev.themaxlive.com",
                "https://master-portal-dev.themaxlive.com"
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
    }

}
