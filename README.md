# live-platform-spring-gateway

API gateway for a live video and audio streaming platform built for a client in 2024-25 (AK2 Technologies). One of four Spring Boot services - business, streaming, content management, and this gateway - that sat behind a Flutter mobile app and a Next.js admin portal. Platform rights were sold to the client at the end of the engagement; this repository is published as a code sample.

## What it does

- Single entry point for every client call, routing by path prefix to the business, streaming, and content-management services (Spring Cloud Gateway on WebFlux).
- Validates Keycloak-issued JWTs as an OAuth2 resource server; a small allow-list of public paths (device registration, registration completion, resource upload, health) bypasses authentication.
- Optional mutual TLS at the edge (PKCS12 key store and trust store, TLS 1.2) - disabled by default for local runs.
- Reactive throughout: Spring WebFlux, reactive MongoDB and Redis starters, no blocking I/O on the request path.
- Actuator endpoints for health, metrics, and the gateway route table.
- Spring AOP for cross-cutting error handling, wrapping route handlers to produce a consistent error response shape.

## Stack

Java 17 · Spring Boot 2.7.4 · Spring Cloud 2021.0.4 (Gateway) · Spring Security OAuth2 resource server · Reactive MongoDB and Redis · Actuator · Lombok · ModelMapper · Gradle 7.6 · Docker (multi-stage) · GitLab CI

The sibling streaming service is at github.com/neoashraf/live-platform-spring-streaming (Spring Boot 3.2, Micrometer/Prometheus, Zipkin, Sentry, Loki, Firebase Admin, Agora).

## Layout

```
src/main/java/...          gateway application, security config, route filters
src/main/resources/        application.properties and per-environment profiles
scripts/                   helper scripts for build and deploy
Dockerfile                 two-stage build: gradle:7.6-jdk17 -> openjdk:17, layered jar
.gitlab-ci.yml             build_dev -> build_prod -> deploy_dev -> deploy_prod, versioned Docker images
```

## Running locally

Requirements: JDK 17 and the downstream services (or stubs) reachable on the configured routes.

```
export SPRING_PROFILES_ACTIVE=local
./gradlew bootRun
```

Per-environment values - Keycloak issuer/realm, downstream service URLs, the Mongo URI, the TLS store paths and passwords - are set directly in `application-<profile>.properties` (`local`, `dev`, `a-live-dev`, `prod`) rather than read from the shell environment; `SPRING_PROFILES_ACTIVE` picks which profile loads (it defaults to `a-live-dev` in `application.properties` if unset). The TLS key/trust store (`server-keystore.jks`) and its password are committed in `application.properties` as-is in this sample - in a real deployment these belong in environment variables or a secrets manager, not in the repo.

Health check: `GET /actuator/health`. Route table: `GET /actuator/gateway/routes`.

## Build and deploy

```
docker build -t live-platform-spring-gateway .
```

The GitLab pipeline builds a Docker image per branch (`dev`, `main`), increments the version recorded in the environment `.env` file, pushes the image, and commits the version bump back to the branch. Deployment ran on AWS EC2 with TLS terminated on the host.

## Author

Ashraf Uddin - architecture, gateway, security configuration, and streaming APIs. linkedin.com/in/ashraf-uddin-688143a1
