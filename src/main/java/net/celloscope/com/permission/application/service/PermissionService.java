package net.celloscope.com.permission.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.celloscope.com.permission.application.port.in.PermissionUseCase;
import net.celloscope.com.permission.application.port.out.PermissionPersistencePort;
import net.celloscope.com.permission.domain.Permission;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionService implements PermissionUseCase {

//    private final PermissionPersistencePort persistencePort;

//    @Override
//    public Flux<Permission> getAllPermissionData() {
//        return persistencePort.getAllPermissionList();
//    }

    @Override
    public Flux<Permission> getAllPermissionData() {
        return null;
    }
}
