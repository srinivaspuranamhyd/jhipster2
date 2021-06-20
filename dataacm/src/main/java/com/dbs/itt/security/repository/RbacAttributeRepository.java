package com.dbs.itt.security.repository;

import com.dbs.itt.security.domain.RbacAttribute;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the RbacAttribute entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RbacAttributeRepository extends R2dbcRepository<RbacAttribute, Long>, RbacAttributeRepositoryInternal {
    Flux<RbacAttribute> findAllBy(Pageable pageable);

    @Query("SELECT * FROM rbac_attribute entity WHERE entity.rbac_policy_id = :id")
    Flux<RbacAttribute> findByRbacPolicy(Long id);

    @Query("SELECT * FROM rbac_attribute entity WHERE entity.rbac_policy_id IS NULL")
    Flux<RbacAttribute> findAllWhereRbacPolicyIsNull();

    @Query("SELECT * FROM rbac_attribute entity WHERE entity.rbac_permission_id = :id")
    Flux<RbacAttribute> findByRbacPermission(Long id);

    @Query("SELECT * FROM rbac_attribute entity WHERE entity.rbac_permission_id IS NULL")
    Flux<RbacAttribute> findAllWhereRbacPermissionIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<RbacAttribute> findAll();

    @Override
    Mono<RbacAttribute> findById(Long id);

    @Override
    <S extends RbacAttribute> Mono<S> save(S entity);
}

interface RbacAttributeRepositoryInternal {
    <S extends RbacAttribute> Mono<S> insert(S entity);
    <S extends RbacAttribute> Mono<S> save(S entity);
    Mono<Integer> update(RbacAttribute entity);

    Flux<RbacAttribute> findAll();
    Mono<RbacAttribute> findById(Long id);
    Flux<RbacAttribute> findAllBy(Pageable pageable);
    Flux<RbacAttribute> findAllBy(Pageable pageable, Criteria criteria);
}
