package com.dbs.itt.security.web.rest;

import com.dbs.itt.security.domain.RbacPermission;
import com.dbs.itt.security.repository.RbacPermissionRepository;
import com.dbs.itt.security.service.RbacPermissionService;
import com.dbs.itt.security.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.dbs.itt.security.domain.RbacPermission}.
 */
@RestController
@RequestMapping("/api")
public class RbacPermissionResource {

    private final Logger log = LoggerFactory.getLogger(RbacPermissionResource.class);

    private static final String ENTITY_NAME = "dataacmRbacPermission";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RbacPermissionService rbacPermissionService;

    private final RbacPermissionRepository rbacPermissionRepository;

    public RbacPermissionResource(RbacPermissionService rbacPermissionService, RbacPermissionRepository rbacPermissionRepository) {
        this.rbacPermissionService = rbacPermissionService;
        this.rbacPermissionRepository = rbacPermissionRepository;
    }

    /**
     * {@code POST  /rbac-permissions} : Create a new rbacPermission.
     *
     * @param rbacPermission the rbacPermission to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new rbacPermission, or with status {@code 400 (Bad Request)} if the rbacPermission has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/rbac-permissions")
    public Mono<ResponseEntity<RbacPermission>> createRbacPermission(@Valid @RequestBody RbacPermission rbacPermission)
        throws URISyntaxException {
        log.debug("REST request to save RbacPermission : {}", rbacPermission);
        if (rbacPermission.getId() != null) {
            throw new BadRequestAlertException("A new rbacPermission cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return rbacPermissionService
            .save(rbacPermission)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/rbac-permissions/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /rbac-permissions/:id} : Updates an existing rbacPermission.
     *
     * @param id the id of the rbacPermission to save.
     * @param rbacPermission the rbacPermission to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rbacPermission,
     * or with status {@code 400 (Bad Request)} if the rbacPermission is not valid,
     * or with status {@code 500 (Internal Server Error)} if the rbacPermission couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/rbac-permissions/{id}")
    public Mono<ResponseEntity<RbacPermission>> updateRbacPermission(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody RbacPermission rbacPermission
    ) throws URISyntaxException {
        log.debug("REST request to update RbacPermission : {}, {}", id, rbacPermission);
        if (rbacPermission.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, rbacPermission.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return rbacPermissionRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return rbacPermissionService
                        .save(rbacPermission)
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                        .map(
                            result ->
                                ResponseEntity
                                    .ok()
                                    .headers(
                                        HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString())
                                    )
                                    .body(result)
                        );
                }
            );
    }

    /**
     * {@code PATCH  /rbac-permissions/:id} : Partial updates given fields of an existing rbacPermission, field will ignore if it is null
     *
     * @param id the id of the rbacPermission to save.
     * @param rbacPermission the rbacPermission to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rbacPermission,
     * or with status {@code 400 (Bad Request)} if the rbacPermission is not valid,
     * or with status {@code 404 (Not Found)} if the rbacPermission is not found,
     * or with status {@code 500 (Internal Server Error)} if the rbacPermission couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/rbac-permissions/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<RbacPermission>> partialUpdateRbacPermission(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody RbacPermission rbacPermission
    ) throws URISyntaxException {
        log.debug("REST request to partial update RbacPermission partially : {}, {}", id, rbacPermission);
        if (rbacPermission.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, rbacPermission.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return rbacPermissionRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<RbacPermission> result = rbacPermissionService.partialUpdate(rbacPermission);

                    return result
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                        .map(
                            res ->
                                ResponseEntity
                                    .ok()
                                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                                    .body(res)
                        );
                }
            );
    }

    /**
     * {@code GET  /rbac-permissions} : get all the rbacPermissions.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of rbacPermissions in body.
     */
    @GetMapping("/rbac-permissions")
    public Mono<ResponseEntity<List<RbacPermission>>> getAllRbacPermissions(Pageable pageable, ServerHttpRequest request) {
        log.debug("REST request to get a page of RbacPermissions");
        return rbacPermissionService
            .countAll()
            .zipWith(rbacPermissionService.findAll(pageable).collectList())
            .map(
                countWithEntities -> {
                    return ResponseEntity
                        .ok()
                        .headers(
                            PaginationUtil.generatePaginationHttpHeaders(
                                UriComponentsBuilder.fromHttpRequest(request),
                                new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                            )
                        )
                        .body(countWithEntities.getT2());
                }
            );
    }

    /**
     * {@code GET  /rbac-permissions/:id} : get the "id" rbacPermission.
     *
     * @param id the id of the rbacPermission to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the rbacPermission, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/rbac-permissions/{id}")
    public Mono<ResponseEntity<RbacPermission>> getRbacPermission(@PathVariable Long id) {
        log.debug("REST request to get RbacPermission : {}", id);
        Mono<RbacPermission> rbacPermission = rbacPermissionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(rbacPermission);
    }

    /**
     * {@code DELETE  /rbac-permissions/:id} : delete the "id" rbacPermission.
     *
     * @param id the id of the rbacPermission to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/rbac-permissions/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteRbacPermission(@PathVariable Long id) {
        log.debug("REST request to delete RbacPermission : {}", id);
        return rbacPermissionService
            .delete(id)
            .map(
                result ->
                    ResponseEntity
                        .noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
            );
    }
}
