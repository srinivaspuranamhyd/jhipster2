package com.dbs.itt.security.service;

import com.dbs.itt.security.domain.RbacUser;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link RbacUser}.
 */
public interface RbacUserService {
    /**
     * Save a rbacUser.
     *
     * @param rbacUser the entity to save.
     * @return the persisted entity.
     */
    Mono<RbacUser> save(RbacUser rbacUser);

    /**
     * Partially updates a rbacUser.
     *
     * @param rbacUser the entity to update partially.
     * @return the persisted entity.
     */
    Mono<RbacUser> partialUpdate(RbacUser rbacUser);

    /**
     * Get all the rbacUsers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<RbacUser> findAll(Pageable pageable);

    /**
     * Get all the rbacUsers with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<RbacUser> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Returns the number of rbacUsers available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" rbacUser.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<RbacUser> findOne(Long id);

    /**
     * Delete the "id" rbacUser.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
