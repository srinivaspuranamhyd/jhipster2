package com.dbs.itt.security.service;

import com.dbs.itt.security.domain.RbacAttribute;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link RbacAttribute}.
 */
public interface RbacAttributeService {
    /**
     * Save a rbacAttribute.
     *
     * @param rbacAttribute the entity to save.
     * @return the persisted entity.
     */
    Mono<RbacAttribute> save(RbacAttribute rbacAttribute);

    /**
     * Partially updates a rbacAttribute.
     *
     * @param rbacAttribute the entity to update partially.
     * @return the persisted entity.
     */
    Mono<RbacAttribute> partialUpdate(RbacAttribute rbacAttribute);

    /**
     * Get all the rbacAttributes.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<RbacAttribute> findAll(Pageable pageable);

    /**
     * Returns the number of rbacAttributes available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" rbacAttribute.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<RbacAttribute> findOne(Long id);

    /**
     * Delete the "id" rbacAttribute.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
