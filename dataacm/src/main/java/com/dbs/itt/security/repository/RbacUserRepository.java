package com.dbs.itt.security.repository;

import com.dbs.itt.security.domain.RbacUser;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the RbacUser entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RbacUserRepository extends R2dbcRepository<RbacUser, Long>, RbacUserRepositoryInternal {
    Flux<RbacUser> findAllBy(Pageable pageable);

    @Override
    Mono<RbacUser> findOneWithEagerRelationships(Long id);

    @Override
    Flux<RbacUser> findAllWithEagerRelationships();

    @Override
    Flux<RbacUser> findAllWithEagerRelationships(Pageable page);

    @Override
    Mono<Void> deleteById(Long id);

    @Query(
        "SELECT entity.* FROM rbac_user entity JOIN rel_rbac_user__group joinTable ON entity.id = joinTable.rbac_user_id WHERE joinTable.group_id = :id"
    )
    Flux<RbacUser> findByGroup(Long id);

    // just to avoid having unambigous methods
    @Override
    Flux<RbacUser> findAll();

    @Override
    Mono<RbacUser> findById(Long id);

    @Override
    <S extends RbacUser> Mono<S> save(S entity);
}

interface RbacUserRepositoryInternal {
    <S extends RbacUser> Mono<S> insert(S entity);
    <S extends RbacUser> Mono<S> save(S entity);
    Mono<Integer> update(RbacUser entity);

    Flux<RbacUser> findAll();
    Mono<RbacUser> findById(Long id);
    Flux<RbacUser> findAllBy(Pageable pageable);
    Flux<RbacUser> findAllBy(Pageable pageable, Criteria criteria);

    Mono<RbacUser> findOneWithEagerRelationships(Long id);

    Flux<RbacUser> findAllWithEagerRelationships();

    Flux<RbacUser> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
