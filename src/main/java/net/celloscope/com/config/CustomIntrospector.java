package net.celloscope.com.config;

import lombok.extern.slf4j.Slf4j;
import net.celloscope.com.common.dto.ValidateUserResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.ReactiveOpaqueTokenIntrospector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Component
public  class CustomIntrospector implements ReactiveOpaqueTokenIntrospector {
    private final WebClient webClient;

    @Value("${oauth2.resource.userInfoUri}")
    private String authUrl;

    public CustomIntrospector(@Qualifier("authClient") WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Mono<OAuth2AuthenticatedPrincipal> introspect(String token) {
        Map<String, Object> uriParam = new HashMap<>();

        Mono<OAuth2AuthenticatedPrincipal> oAuth2AuthenticatedPrincipalMono = webClient
                .get()
                .uri(authUrl)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .retrieve()
                .bodyToMono(ValidateUserResponse.class)
                .doOnRequest(r -> log.info("Getting auth response : {}", r))
                .doOnError(throwable -> log.error("Failed to validate access token from auth server : {}", throwable.getMessage()))
                .doOnSuccess(response -> log.info("Successfully get response from auth server : {}", response))
                .onErrorMap(throwable -> new InsufficientAuthenticationException(throwable.getMessage()))
                .map(validateUserResponse -> {
                    uriParam.put("USER_ID", validateUserResponse.getName());
                    uriParam.put("username", validateUserResponse.getName());
                    uriParam.put("mfiId", validateUserResponse.getPrincipal().getMfiId());
                    uriParam.put("instituteOid", validateUserResponse.getPrincipal().getInstituteOid());
                    return new DefaultOAuth2AuthenticatedPrincipal(
                            validateUserResponse.getName(), uriParam, extractAuthorities(validateUserResponse));
                });
        return oAuth2AuthenticatedPrincipalMono.doOnSuccess(oAuth2AuthenticatedPrincipal -> log.info("oAuth2AuthenticatedPrincipalMono : {}", oAuth2AuthenticatedPrincipal.getAttributes()));
    }

    private Collection<GrantedAuthority> extractAuthorities(ValidateUserResponse principal) {
        return principal.getAuthorities().stream()
                .map(authorities -> new SimpleGrantedAuthority(authorities.getAuthority()))
                .collect(Collectors.toList());
    }
}