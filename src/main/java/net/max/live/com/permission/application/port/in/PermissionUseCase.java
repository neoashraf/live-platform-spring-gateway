package net.max.live.com.permission.application.port.in;


import net.max.live.com.permission.domain.Permission;
import reactor.core.publisher.Flux;

public interface PermissionUseCase {
    Flux<Permission> getAllPermissionData();

}
