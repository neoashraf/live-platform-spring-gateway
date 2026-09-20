package com.ak2.live.gateway.device;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface DeviceRepository extends ReactiveMongoRepository<DeviceEntity, String> {
    Mono<DeviceEntity> save(DeviceEntity device);
    Mono<DeviceEntity> findByDeviceId(String deviceId);
}
