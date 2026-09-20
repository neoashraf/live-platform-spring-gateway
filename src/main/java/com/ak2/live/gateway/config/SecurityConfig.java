package com.ak2.live.gateway.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.ak2.live.gateway.constant.RoutedPath;
import com.ak2.live.gateway.permission.application.port.in.PermissionUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Configuration
@Slf4j
@EnableWebFluxSecurity
//@EnableResourceServer
@RequiredArgsConstructor
public class SecurityConfig {

    private final PermissionUseCase permissionUseCase ;
    private final CustomIntrospector customIntrospector;
    private final SecurityProperties securityProperties;

    @PostConstruct
    public void init() {
        List<String> allPublicPaths = new ArrayList<>(securityProperties.getPublicPaths());
        allPublicPaths.add(RoutedPath.KEY_CLOAK_WRAPPER_PATH_PATTERN);
        allPublicPaths.add(RoutedPath.KEY_CLOAK_REALM_PATH_PATTERN);

        log.info("All Public Paths at startup: {}", allPublicPaths);
    }


    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        List<String> allPublicPaths = new ArrayList<>(securityProperties.getPublicPaths());
        allPublicPaths.add(RoutedPath.KEY_CLOAK_WRAPPER_PATH_PATTERN);
        allPublicPaths.add(RoutedPath.KEY_CLOAK_REALM_PATH_PATTERN);

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
//                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .headers(headers -> headers.frameOptions(ServerHttpSecurity.HeaderSpec.FrameOptionsSpec::disable))
                .authorizeExchange(exchange -> exchange
                        .pathMatchers(allPublicPaths.toArray(new String[0])).permitAll()
                        .anyExchange().authenticated()
                )
                .exceptionHandling(exceptionHandlingSpec -> exceptionHandlingSpec
                        .authenticationEntryPoint(authenticationEntryPoint())
                        .accessDeniedHandler(accessDeniedHandler())
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .opaqueToken(opaqueToken -> opaqueToken
                                .introspector(customIntrospector)
                        )
                )
                .build();
    }

    private ServerAuthenticationEntryPoint authenticationEntryPoint() {
        return (exchange, ex) -> {
            log.error("Unauthorized request: {}", exchange.getRequest().getURI());
            return new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED).commence(exchange, ex);
        };
    }

    private ServerAccessDeniedHandler accessDeniedHandler() {
        return (exchange, denied) -> {
            log.error("Access denied: {}", exchange.getRequest().getURI());

            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.FORBIDDEN);
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

            String body = "{\"timestamp\":\"" + Instant.now() +
                    "\",\"path\":\"" + exchange.getRequest().getURI().getPath() +
                    "\",\"status\":403,\"error\":\"Forbidden\"}";

            DataBuffer dataBuffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(dataBuffer));
        };
    }


    /*@Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        corsConfiguration.setAllowedOrigins(List.of(
                "https://owner-dev.example.com",
                "https://country-admin-dev.example.com",
                "https://agency-dev.example.com",
                "https://reseller-dev.example.com",
                "https://master-portal-dev.example.com"
                )); // ✅ Specific origin, not "*"

        // ✅ Required for JWTs or sessions sent in requests
        corsConfiguration.setAllowCredentials(true);

        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        corsConfiguration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        corsConfiguration.setMaxAge(8000L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }*/

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(List.of("*"));
        corsConfiguration.setAllowedMethods(List.of("*"));
        corsConfiguration.setAllowedHeaders(List.of( "*" ));
        corsConfiguration.setMaxAge(8000L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

}


