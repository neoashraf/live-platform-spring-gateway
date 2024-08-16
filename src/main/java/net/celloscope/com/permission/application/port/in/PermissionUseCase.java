package net.celloscope.com.permission.application.port.in;


import net.celloscope.com.permission.domain.Permission;
import reactor.core.publisher.Flux;

public interface PermissionUseCase {
    Flux<Permission> getAllPermissionData();

}
