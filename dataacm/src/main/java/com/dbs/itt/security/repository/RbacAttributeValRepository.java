package com.dbs.itt.security.repository;

import com.dbs.itt.security.domain.RbacAttributeVal;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the RbacAttributeVal entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RbacAttributeValRepository extends R2dbcRepository<RbacAttributeVal, Long>, RbacAttributeValRepositoryInternal {
    Flux<RbacAttributeVal> findAllBy(Pageable pageable);

    @Query("SELECT * FROM rbac_attribute_val entity WHERE entity.attr_id = :id")
    Flux<RbacAttributeVal> findByAttr(Long id);

    @Query("SELECT * FROM rbac_attribute_val entity WHERE entity.attr_id IS NULL")
    Flux<RbacAttributeVal> findAllWhereAttrIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<RbacAttributeVal> findAll();

    @Override
    Mono<RbacAttributeVal> findById(Long id);

    @Override
    <S extends RbacAttributeVal> Mono<S> save(S entity);
}

interface RbacAttributeValRepositoryInternal {
    <S extends RbacAttributeVal> Mono<S> insert(S entity);
    <S extends RbacAttributeVal> Mono<S> save(S entity);
    Mono<Integer> update(RbacAttributeVal entity);

    Flux<RbacAttributeVal> findAll();
    Mono<RbacAttributeVal> findById(Long id);
    Flux<RbacAttributeVal> findAllBy(Pageable pageable);
    Flux<RbacAttributeVal> findAllBy(Pageable pageable, Criteria criteria);
}
