package com.dbs.itt.security.repository;

import com.dbs.itt.security.domain.RbacPermission;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the RbacPermission entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RbacPermissionRepository extends R2dbcRepository<RbacPermission, Long>, RbacPermissionRepositoryInternal {
    Flux<RbacPermission> findAllBy(Pageable pageable);

    @Query("SELECT * FROM rbac_permission entity WHERE entity.approval_id = :id")
    Flux<RbacPermission> findByApproval(Long id);

    @Query("SELECT * FROM rbac_permission entity WHERE entity.approval_id IS NULL")
    Flux<RbacPermission> findAllWhereApprovalIsNull();

    @Query("SELECT * FROM rbac_permission entity WHERE entity.user_id = :id")
    Flux<RbacPermission> findByUser(Long id);

    @Query("SELECT * FROM rbac_permission entity WHERE entity.user_id IS NULL")
    Flux<RbacPermission> findAllWhereUserIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<RbacPermission> findAll();

    @Override
    Mono<RbacPermission> findById(Long id);

    @Override
    <S extends RbacPermission> Mono<S> save(S entity);
}

interface RbacPermissionRepositoryInternal {
    <S extends RbacPermission> Mono<S> insert(S entity);
    <S extends RbacPermission> Mono<S> save(S entity);
    Mono<Integer> update(RbacPermission entity);

    Flux<RbacPermission> findAll();
    Mono<RbacPermission> findById(Long id);
    Flux<RbacPermission> findAllBy(Pageable pageable);
    Flux<RbacPermission> findAllBy(Pageable pageable, Criteria criteria);
}
