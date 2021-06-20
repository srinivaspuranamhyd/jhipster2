package com.dbs.itt.security.service;

import com.dbs.itt.security.domain.RbacSecurityGroup;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link RbacSecurityGroup}.
 */
public interface RbacSecurityGroupService {
    /**
     * Save a rbacSecurityGroup.
     *
     * @param rbacSecurityGroup the entity to save.
     * @return the persisted entity.
     */
    Mono<RbacSecurityGroup> save(RbacSecurityGroup rbacSecurityGroup);

    /**
     * Partially updates a rbacSecurityGroup.
     *
     * @param rbacSecurityGroup the entity to update partially.
     * @return the persisted entity.
     */
    Mono<RbacSecurityGroup> partialUpdate(RbacSecurityGroup rbacSecurityGroup);

    /**
     * Get all the rbacSecurityGroups.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<RbacSecurityGroup> findAll(Pageable pageable);

    /**
     * Get all the rbacSecurityGroups with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<RbacSecurityGroup> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Returns the number of rbacSecurityGroups available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" rbacSecurityGroup.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<RbacSecurityGroup> findOne(Long id);

    /**
     * Delete the "id" rbacSecurityGroup.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
