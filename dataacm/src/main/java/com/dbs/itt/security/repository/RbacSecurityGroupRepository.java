package com.dbs.itt.security.repository;

import com.dbs.itt.security.domain.RbacSecurityGroup;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the RbacSecurityGroup entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RbacSecurityGroupRepository extends R2dbcRepository<RbacSecurityGroup, Long>, RbacSecurityGroupRepositoryInternal {
    Flux<RbacSecurityGroup> findAllBy(Pageable pageable);

    @Override
    Mono<RbacSecurityGroup> findOneWithEagerRelationships(Long id);

    @Override
    Flux<RbacSecurityGroup> findAllWithEagerRelationships();

    @Override
    Flux<RbacSecurityGroup> findAllWithEagerRelationships(Pageable page);

    @Override
    Mono<Void> deleteById(Long id);

    @Query(
        "SELECT entity.* FROM rbac_security_group entity JOIN rel_rbac_security_group__data_topic joinTable ON entity.id = joinTable.rbac_security_group_id WHERE joinTable.data_topic_id = :id"
    )
    Flux<RbacSecurityGroup> findByDataTopic(Long id);

    // just to avoid having unambigous methods
    @Override
    Flux<RbacSecurityGroup> findAll();

    @Override
    Mono<RbacSecurityGroup> findById(Long id);

    @Override
    <S extends RbacSecurityGroup> Mono<S> save(S entity);
}

interface RbacSecurityGroupRepositoryInternal {
    <S extends RbacSecurityGroup> Mono<S> insert(S entity);
    <S extends RbacSecurityGroup> Mono<S> save(S entity);
    Mono<Integer> update(RbacSecurityGroup entity);

    Flux<RbacSecurityGroup> findAll();
    Mono<RbacSecurityGroup> findById(Long id);
    Flux<RbacSecurityGroup> findAllBy(Pageable pageable);
    Flux<RbacSecurityGroup> findAllBy(Pageable pageable, Criteria criteria);

    Mono<RbacSecurityGroup> findOneWithEagerRelationships(Long id);

    Flux<RbacSecurityGroup> findAllWithEagerRelationships();

    Flux<RbacSecurityGroup> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
