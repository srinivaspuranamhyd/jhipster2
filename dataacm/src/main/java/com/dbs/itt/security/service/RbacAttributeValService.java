package com.dbs.itt.security.service;

import com.dbs.itt.security.domain.RbacAttributeVal;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link RbacAttributeVal}.
 */
public interface RbacAttributeValService {
    /**
     * Save a rbacAttributeVal.
     *
     * @param rbacAttributeVal the entity to save.
     * @return the persisted entity.
     */
    Mono<RbacAttributeVal> save(RbacAttributeVal rbacAttributeVal);

    /**
     * Partially updates a rbacAttributeVal.
     *
     * @param rbacAttributeVal the entity to update partially.
     * @return the persisted entity.
     */
    Mono<RbacAttributeVal> partialUpdate(RbacAttributeVal rbacAttributeVal);

    /**
     * Get all the rbacAttributeVals.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<RbacAttributeVal> findAll(Pageable pageable);

    /**
     * Returns the number of rbacAttributeVals available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" rbacAttributeVal.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<RbacAttributeVal> findOne(Long id);

    /**
     * Delete the "id" rbacAttributeVal.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
