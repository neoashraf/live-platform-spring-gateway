//package com.ak2.live.gateway.permission.adapter.out.persistence.repository;
//
//
//import com.ak2.live.gateway.permission.adapter.out.persistence.entity.PermissionEntity;
//import org.springframework.data.repository.reactive.ReactiveCrudRepository;
//import reactor.core.publisher.Flux;
//
//public interface PermissionRepository extends ReactiveCrudRepository<PermissionEntity, String> {
//
//    Flux<PermissionEntity> findAllByPermissionNameIsNotNull();
//}
