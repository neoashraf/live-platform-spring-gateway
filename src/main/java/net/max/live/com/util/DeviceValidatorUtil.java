package net.max.live.com.util;

import lombok.RequiredArgsConstructor;
import net.max.live.com.device.DeviceEntity;
import net.max.live.com.device.DeviceRepository;
import net.max.live.com.enums.Constants;
import net.max.live.com.util.exception.ExceptionHandlerUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class DeviceValidatorUtil {

    private final DeviceRepository deviceRepository;

    // return error if device not found
    public Mono<Void> validateDeviceOrThrow(ServerWebExchange exchange) {
        String deviceId = exchange.getRequest().getHeaders().getFirst("X-Device-Id");
        String appVersion = exchange.getRequest().getHeaders().getFirst("X-App-Version");

        if (deviceId == null || deviceId.isBlank()) {
            return Mono.error(new ExceptionHandlerUtil(HttpStatus.BAD_REQUEST, "Missing required header: X-Device-Id"));
        }

        if (appVersion == null || appVersion.isBlank()) {
            return Mono.error(new ExceptionHandlerUtil(HttpStatus.BAD_REQUEST, "Missing required header: X-App-Version"));
        }

        return deviceRepository.findByDeviceId(deviceId)
                .switchIfEmpty(Mono.error(new ExceptionHandlerUtil(HttpStatus.FORBIDDEN, "Device not found")))
                .flatMap(device -> {
                    if (Constants.STATUS_NO.getValue().equalsIgnoreCase(device.getActive())) {
                        return Mono.error(new ExceptionHandlerUtil(HttpStatus.FORBIDDEN, "Device is banned!"));
                    }
                    return Mono.empty(); // valid device
                });
    }

    public Mono<Void> validateDeviceDoesNotExistOrNotBanned(ServerWebExchange exchange) {
        String deviceId = exchange.getRequest().getHeaders().getFirst("X-Device-Id");
        String appVersion = exchange.getRequest().getHeaders().getFirst("X-App-Version");

        if (deviceId == null || deviceId.isBlank()) {
            return Mono.error(new ExceptionHandlerUtil(HttpStatus.BAD_REQUEST, "Missing required header: deviceId"));
        }

        if (appVersion == null || appVersion.isBlank()) {
            return Mono.error(new ExceptionHandlerUtil(HttpStatus.BAD_REQUEST, "Missing required header: X-App-Version"));
        }

        return deviceRepository.findByDeviceId(deviceId)
                .switchIfEmpty(Mono.just(DeviceEntity.builder().active(Constants.STATUS_YES.getValue()).build()))
                .flatMap(device -> {
                    if (Constants.STATUS_NO.getValue().equalsIgnoreCase(device.getActive())) {
                        return Mono.error(new ExceptionHandlerUtil(HttpStatus.FORBIDDEN, "Device is banned!"));
                    }
                    return Mono.empty(); // valid device
                });
    }


}

