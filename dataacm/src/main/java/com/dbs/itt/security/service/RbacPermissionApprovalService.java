package com.dbs.itt.security.service;

import com.dbs.itt.security.domain.RbacPermissionApproval;
import java.util.List;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link RbacPermissionApproval}.
 */
public interface RbacPermissionApprovalService {
    /**
     * Save a rbacPermissionApproval.
     *
     * @param rbacPermissionApproval the entity to save.
     * @return the persisted entity.
     */
    Mono<RbacPermissionApproval> save(RbacPermissionApproval rbacPermissionApproval);

    /**
     * Partially updates a rbacPermissionApproval.
     *
     * @param rbacPermissionApproval the entity to update partially.
     * @return the persisted entity.
     */
    Mono<RbacPermissionApproval> partialUpdate(RbacPermissionApproval rbacPermissionApproval);

    /**
     * Get all the rbacPermissionApprovals.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<RbacPermissionApproval> findAll(Pageable pageable);
    /**
     * Get all the RbacPermissionApproval where Permission is {@code null}.
     *
     * @return the {@link Flux} of entities.
     */
    Flux<RbacPermissionApproval> findAllWherePermissionIsNull();

    /**
     * Returns the number of rbacPermissionApprovals available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" rbacPermissionApproval.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<RbacPermissionApproval> findOne(Long id);

    /**
     * Delete the "id" rbacPermissionApproval.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
