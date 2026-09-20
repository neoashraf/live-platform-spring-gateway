/*
package com.ak2.live.gateway.config;

import constant.com.ak2.live.gateway.RoutedPath;
import constant.com.ak2.live.gateway.URI;
import filter.com.ak2.live.gateway.QueryParamFilter;
import filter.com.ak2.live.gateway.ResponseLogFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Slf4j
@Configuration
public class SpringCloudConfig {


    @Bean
    public RouteLocator myRoutes(RouteLocatorBuilder builder, QueryParamFilter queryParamFilter, ResponseLogFilter responseLogFilter) {
        return
            builder
            .routes()
            .route(r -> r
                .path(RoutedPath.KEY_CLOAK_REALM_PATH_PATTERN)
                .uri(URI.KEY_CLOAK_URI))
            */
/*.route(r -> r
                .path(RoutedPath.CORECORRECT_PATH_PATTERN)
                .filters(f -> f
                        .filter(RequestModifyFilter.apply(new RequestModifyFilter.Config()))
                )
                .uri(URI.CORECORRECT_URI))*//*

            .route(r -> r
                    .path(RoutedPath.OAUTH_PATTERN)
                    .uri(URI.AUTH_SERVER_URI))
            .route(r -> r
                .path(RoutedPath.LOAN_PORTFOLIO_PATH_PATTERN)
                .filters(f -> f
                        .filter(queryParamFilter.apply(new QueryParamFilter.Config()))
                )
                .uri(URI.LOAN_PORTFOLIO_URI))
            .route(r -> r
                    .path(RoutedPath.COMMON_PATH_PATTERN)
                    .filters(f -> f
                            .filter(queryParamFilter.apply(new QueryParamFilter.Config()))
                            .filter(responseLogFilter.apply(new ResponseLogFilter.Config()))
                    )
                    .uri(URI.COMMON_SERVICE_URI))
            .build();
    }

}*/
