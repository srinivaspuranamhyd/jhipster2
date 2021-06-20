package com.dbs.itt.security.repository;

import com.dbs.itt.security.domain.RbacPermissionApproval;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the RbacPermissionApproval entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RbacPermissionApprovalRepository
    extends R2dbcRepository<RbacPermissionApproval, Long>, RbacPermissionApprovalRepositoryInternal {
    Flux<RbacPermissionApproval> findAllBy(Pageable pageable);

    @Query("SELECT * FROM rbac_permission_approval entity WHERE entity.id not in (select approval_id from rbac_permission)")
    Flux<RbacPermissionApproval> findAllWherePermissionIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<RbacPermissionApproval> findAll();

    @Override
    Mono<RbacPermissionApproval> findById(Long id);

    @Override
    <S extends RbacPermissionApproval> Mono<S> save(S entity);
}

interface RbacPermissionApprovalRepositoryInternal {
    <S extends RbacPermissionApproval> Mono<S> insert(S entity);
    <S extends RbacPermissionApproval> Mono<S> save(S entity);
    Mono<Integer> update(RbacPermissionApproval entity);

    Flux<RbacPermissionApproval> findAll();
    Mono<RbacPermissionApproval> findById(Long id);
    Flux<RbacPermissionApproval> findAllBy(Pageable pageable);
    Flux<RbacPermissionApproval> findAllBy(Pageable pageable, Criteria criteria);
}
