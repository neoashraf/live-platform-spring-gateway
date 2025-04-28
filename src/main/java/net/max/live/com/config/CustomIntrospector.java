package net.max.live.com.config;

import lombok.extern.slf4j.Slf4j;
import net.max.live.com.common.dto.ValidateUserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.server.resource.introspection.ReactiveOpaqueTokenIntrospector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@Component
public  class CustomIntrospector implements ReactiveOpaqueTokenIntrospector {
    private final WebClient webClient;
    private final JwtDecoder jwtDecoder;

    @Value("${oauth2.resource.userInfoUri}")
    private String authUrl;

    public CustomIntrospector(@Qualifier("authClient") WebClient webClient, JwtDecoder jwtDecoder) {
        this.webClient = webClient;
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    public Mono<OAuth2AuthenticatedPrincipal> introspect(String token) {

        // Check if token is likely a JWT (simple check: three parts separated by '.')
        if (token.split("\\.").length == 3) {
            log.info("Token appears to be a JWT, decoding locally.");

            Jwt jwt = jwtDecoder.decode(token);
            Map<String, Object> claims = jwt.getClaims();
            log.info("Decoded JWT claims: {}", claims);

            Map<String, Object> uriParam = new HashMap<>();
            uriParam.put("keycloakId", claims.get("sub"));
            uriParam.put("username", claims.get("preferred_username"));
            uriParam.put("email", claims.get("email"));
            uriParam.put("name", claims.get("name"));
            if (claims.containsKey("azp")) {
                log.info("azp exists: {}", claims.get("azp"));
                uriParam.put("azp", claims.get("azp"));
            }

            return Mono.just(new DefaultOAuth2AuthenticatedPrincipal(
                    (String) claims.get("name"), uriParam, Collections.emptyList()));
        }

        // Otherwise, fallback to calling the userinfo endpoint (like you originally did)
        log.info("Token does not appear to be a JWT. Using WebClient to introspect.");

        return webClient
                .get()
                .uri(authUrl)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .retrieve()
                .bodyToMono(ValidateUserResponse.class)
                .doOnNext(validateUserResponse -> log.info("Introspected token via WebClient: {}", validateUserResponse))
                .onErrorMap(throwable -> new InsufficientAuthenticationException(throwable.getMessage()))
                .map(validateUserResponse -> {
                    Map<String, Object> uriParam = new HashMap<>();
                    uriParam.put("keycloakId", validateUserResponse.getSub());
                    uriParam.put("username", validateUserResponse.getPreferredUsername());
                    uriParam.put("email", validateUserResponse.getEmail());
                    uriParam.put("name", validateUserResponse.getName());
                    if (validateUserResponse.getAzp() != null) {
                        uriParam.put("azp", validateUserResponse.getAzp());
                    }

                    return new DefaultOAuth2AuthenticatedPrincipal(
                            validateUserResponse.getName(), uriParam, extractAuthorities(validateUserResponse));
                });
    }


    private Collection<GrantedAuthority> extractAuthorities(ValidateUserResponse principal) {
        return null;
//        return principal.getAuthorities().stream()
//                .map(authorities -> new SimpleGrantedAuthority(authorities.getAuthority()))
//                .collect(Collectors.toList());
    }
}