package net.celloscope.com.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.celloscope.com.constant.RoutedPath;
import net.celloscope.com.permission.application.port.in.PermissionUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Configuration
@Slf4j
@EnableWebFluxSecurity
//@EnableResourceServer
@RequiredArgsConstructor
public class SecurityConfig {

    private final PermissionUseCase permissionUseCase ;
    private final CustomIntrospector customIntrospector;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity httpSecurity) {
        httpSecurity
                .oauth2ResourceServer(oauth2 -> oauth2
                .opaqueToken(opaqueToken -> opaqueToken
                        .introspector(customIntrospector)))
                .csrf().disable()
                .authorizeExchange().pathMatchers(
                        "/management/**",
                        "/actuator/**",
                        "/mra-ims/common/api/v1/files/download/**",
                        RoutedPath.KEY_CLOAK_REALM_PATH_PATTERN
                )
                .permitAll()
        ;

//        Objects.requireNonNull(permissionUseCase.getAllPermissionData().collectList().block())
//                .forEach( permission -> {
//                    log.debug("permission ++ {}", permission);
//                    if(permission.getPermissionName().equalsIgnoreCase("All"))
//                        httpSecurity.authorizeExchange().pathMatchers(permission.getMethod(), permission.getUrl()).permitAll();
//                    else
//                        httpSecurity.authorizeExchange().pathMatchers(permission.getMethod(), permission.getUrl())
//                                .hasAnyAuthority(permission.getPermissionName());
//                });

        httpSecurity.authorizeExchange()
                .anyExchange().authenticated()
                .and()
                .headers()
                .frameOptions().disable()
//                .and()
//                .sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        ;
        return httpSecurity.build();
    }

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


