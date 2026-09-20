package com.ak2.live.gateway.permission.application.port.in;


import com.ak2.live.gateway.permission.domain.Permission;
import reactor.core.publisher.Flux;

public interface PermissionUseCase {
    Flux<Permission> getAllPermissionData();

}
