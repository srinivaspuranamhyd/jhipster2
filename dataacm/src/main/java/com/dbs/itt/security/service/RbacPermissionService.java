package com.dbs.itt.security.service;

import com.dbs.itt.security.domain.RbacPermission;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link RbacPermission}.
 */
public interface RbacPermissionService {
    /**
     * Save a rbacPermission.
     *
     * @param rbacPermission the entity to save.
     * @return the persisted entity.
     */
    Mono<RbacPermission> save(RbacPermission rbacPermission);

    /**
     * Partially updates a rbacPermission.
     *
     * @param rbacPermission the entity to update partially.
     * @return the persisted entity.
     */
    Mono<RbacPermission> partialUpdate(RbacPermission rbacPermission);

    /**
     * Get all the rbacPermissions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<RbacPermission> findAll(Pageable pageable);

    /**
     * Returns the number of rbacPermissions available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" rbacPermission.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<RbacPermission> findOne(Long id);

    /**
     * Delete the "id" rbacPermission.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
