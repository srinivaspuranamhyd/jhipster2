package com.dbs.itt.security.repository;

import com.dbs.itt.security.domain.RbacPolicy;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the RbacPolicy entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RbacPolicyRepository extends R2dbcRepository<RbacPolicy, Long>, RbacPolicyRepositoryInternal {
    Flux<RbacPolicy> findAllBy(Pageable pageable);

    @Query("SELECT * FROM rbac_policy entity WHERE entity.user_id = :id")
    Flux<RbacPolicy> findByUser(Long id);

    @Query("SELECT * FROM rbac_policy entity WHERE entity.user_id IS NULL")
    Flux<RbacPolicy> findAllWhereUserIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<RbacPolicy> findAll();

    @Override
    Mono<RbacPolicy> findById(Long id);

    @Override
    <S extends RbacPolicy> Mono<S> save(S entity);
}

interface RbacPolicyRepositoryInternal {
    <S extends RbacPolicy> Mono<S> insert(S entity);
    <S extends RbacPolicy> Mono<S> save(S entity);
    Mono<Integer> update(RbacPolicy entity);

    Flux<RbacPolicy> findAll();
    Mono<RbacPolicy> findById(Long id);
    Flux<RbacPolicy> findAllBy(Pageable pageable);
    Flux<RbacPolicy> findAllBy(Pageable pageable, Criteria criteria);
}
