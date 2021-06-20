package com.dbs.itt.security.service;

import com.dbs.itt.security.domain.RbacDataAccess;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link RbacDataAccess}.
 */
public interface RbacDataAccessService {
    /**
     * Save a rbacDataAccess.
     *
     * @param rbacDataAccess the entity to save.
     * @return the persisted entity.
     */
    Mono<RbacDataAccess> save(RbacDataAccess rbacDataAccess);

    /**
     * Partially updates a rbacDataAccess.
     *
     * @param rbacDataAccess the entity to update partially.
     * @return the persisted entity.
     */
    Mono<RbacDataAccess> partialUpdate(RbacDataAccess rbacDataAccess);

    /**
     * Get all the rbacDataAccesses.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<RbacDataAccess> findAll(Pageable pageable);

    /**
     * Returns the number of rbacDataAccesses available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" rbacDataAccess.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<RbacDataAccess> findOne(Long id);

    /**
     * Delete the "id" rbacDataAccess.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
