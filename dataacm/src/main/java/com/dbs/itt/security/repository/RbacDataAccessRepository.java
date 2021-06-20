package com.dbs.itt.security.repository;

import com.dbs.itt.security.domain.RbacDataAccess;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the RbacDataAccess entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RbacDataAccessRepository extends R2dbcRepository<RbacDataAccess, Long>, RbacDataAccessRepositoryInternal {
    Flux<RbacDataAccess> findAllBy(Pageable pageable);

    // just to avoid having unambigous methods
    @Override
    Flux<RbacDataAccess> findAll();

    @Override
    Mono<RbacDataAccess> findById(Long id);

    @Override
    <S extends RbacDataAccess> Mono<S> save(S entity);
}

interface RbacDataAccessRepositoryInternal {
    <S extends RbacDataAccess> Mono<S> insert(S entity);
    <S extends RbacDataAccess> Mono<S> save(S entity);
    Mono<Integer> update(RbacDataAccess entity);

    Flux<RbacDataAccess> findAll();
    Mono<RbacDataAccess> findById(Long id);
    Flux<RbacDataAccess> findAllBy(Pageable pageable);
    Flux<RbacDataAccess> findAllBy(Pageable pageable, Criteria criteria);
}
