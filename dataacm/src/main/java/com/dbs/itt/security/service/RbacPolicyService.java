package com.dbs.itt.security.service;

import com.dbs.itt.security.domain.RbacPolicy;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link RbacPolicy}.
 */
public interface RbacPolicyService {
    /**
     * Save a rbacPolicy.
     *
     * @param rbacPolicy the entity to save.
     * @return the persisted entity.
     */
    Mono<RbacPolicy> save(RbacPolicy rbacPolicy);

    /**
     * Partially updates a rbacPolicy.
     *
     * @param rbacPolicy the entity to update partially.
     * @return the persisted entity.
     */
    Mono<RbacPolicy> partialUpdate(RbacPolicy rbacPolicy);

    /**
     * Get all the rbacPolicies.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<RbacPolicy> findAll(Pageable pageable);

    /**
     * Returns the number of rbacPolicies available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" rbacPolicy.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<RbacPolicy> findOne(Long id);

    /**
     * Delete the "id" rbacPolicy.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
