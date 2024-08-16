//package net.celloscope.com.permission.adapter.out.persistence;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import net.celloscope.com.permission.adapter.out.persistence.repository.PermissionRepository;
//import net.celloscope.com.permission.application.port.out.PermissionPersistencePort;
//import net.celloscope.com.permission.domain.Permission;
//import org.modelmapper.ModelMapper;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Component;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class PermissionPersistenceAdapter implements PermissionPersistencePort {
//    private final PermissionRepository repository;
//    private final ModelMapper modelMapper;
//
//    @Override
//    public Flux<Permission> getAllPermissionList() {
//        return repository.findAllByPermissionNameIsNotNull()
//                .map(permissionEntity -> modelMapper.map(permissionEntity, Permission.class))
////                .switchIfEmpty(Mono.error(new ExceptionHandlerUtil(HttpStatus.NOT_FOUND, "Not found")))
//                .switchIfEmpty(Flux.empty())
//                .doOnRequest(request -> log.info("Getting permission list from db : {}", request))
//                .doOnError(e -> log.error("Error occurred fetching data from db: {}", e.getMessage()));
//    }
//}
