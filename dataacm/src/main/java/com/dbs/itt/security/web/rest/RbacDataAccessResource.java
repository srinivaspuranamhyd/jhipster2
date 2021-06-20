package com.dbs.itt.security.web.rest;

import com.dbs.itt.security.domain.RbacDataAccess;
import com.dbs.itt.security.repository.RbacDataAccessRepository;
import com.dbs.itt.security.service.RbacDataAccessService;
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
 * REST controller for managing {@link com.dbs.itt.security.domain.RbacDataAccess}.
 */
@RestController
@RequestMapping("/api")
public class RbacDataAccessResource {

    private final Logger log = LoggerFactory.getLogger(RbacDataAccessResource.class);

    private static final String ENTITY_NAME = "dataacmRbacDataAccess";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RbacDataAccessService rbacDataAccessService;

    private final RbacDataAccessRepository rbacDataAccessRepository;

    public RbacDataAccessResource(RbacDataAccessService rbacDataAccessService, RbacDataAccessRepository rbacDataAccessRepository) {
        this.rbacDataAccessService = rbacDataAccessService;
        this.rbacDataAccessRepository = rbacDataAccessRepository;
    }

    /**
     * {@code POST  /rbac-data-accesses} : Create a new rbacDataAccess.
     *
     * @param rbacDataAccess the rbacDataAccess to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new rbacDataAccess, or with status {@code 400 (Bad Request)} if the rbacDataAccess has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/rbac-data-accesses")
    public Mono<ResponseEntity<RbacDataAccess>> createRbacDataAccess(@Valid @RequestBody RbacDataAccess rbacDataAccess)
        throws URISyntaxException {
        log.debug("REST request to save RbacDataAccess : {}", rbacDataAccess);
        if (rbacDataAccess.getId() != null) {
            throw new BadRequestAlertException("A new rbacDataAccess cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return rbacDataAccessService
            .save(rbacDataAccess)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/rbac-data-accesses/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /rbac-data-accesses/:id} : Updates an existing rbacDataAccess.
     *
     * @param id the id of the rbacDataAccess to save.
     * @param rbacDataAccess the rbacDataAccess to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rbacDataAccess,
     * or with status {@code 400 (Bad Request)} if the rbacDataAccess is not valid,
     * or with status {@code 500 (Internal Server Error)} if the rbacDataAccess couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/rbac-data-accesses/{id}")
    public Mono<ResponseEntity<RbacDataAccess>> updateRbacDataAccess(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody RbacDataAccess rbacDataAccess
    ) throws URISyntaxException {
        log.debug("REST request to update RbacDataAccess : {}, {}", id, rbacDataAccess);
        if (rbacDataAccess.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, rbacDataAccess.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return rbacDataAccessRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return rbacDataAccessService
                        .save(rbacDataAccess)
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
     * {@code PATCH  /rbac-data-accesses/:id} : Partial updates given fields of an existing rbacDataAccess, field will ignore if it is null
     *
     * @param id the id of the rbacDataAccess to save.
     * @param rbacDataAccess the rbacDataAccess to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rbacDataAccess,
     * or with status {@code 400 (Bad Request)} if the rbacDataAccess is not valid,
     * or with status {@code 404 (Not Found)} if the rbacDataAccess is not found,
     * or with status {@code 500 (Internal Server Error)} if the rbacDataAccess couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/rbac-data-accesses/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<RbacDataAccess>> partialUpdateRbacDataAccess(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody RbacDataAccess rbacDataAccess
    ) throws URISyntaxException {
        log.debug("REST request to partial update RbacDataAccess partially : {}, {}", id, rbacDataAccess);
        if (rbacDataAccess.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, rbacDataAccess.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return rbacDataAccessRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<RbacDataAccess> result = rbacDataAccessService.partialUpdate(rbacDataAccess);

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
     * {@code GET  /rbac-data-accesses} : get all the rbacDataAccesses.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of rbacDataAccesses in body.
     */
    @GetMapping("/rbac-data-accesses")
    public Mono<ResponseEntity<List<RbacDataAccess>>> getAllRbacDataAccesses(Pageable pageable, ServerHttpRequest request) {
        log.debug("REST request to get a page of RbacDataAccesses");
        return rbacDataAccessService
            .countAll()
            .zipWith(rbacDataAccessService.findAll(pageable).collectList())
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
     * {@code GET  /rbac-data-accesses/:id} : get the "id" rbacDataAccess.
     *
     * @param id the id of the rbacDataAccess to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the rbacDataAccess, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/rbac-data-accesses/{id}")
    public Mono<ResponseEntity<RbacDataAccess>> getRbacDataAccess(@PathVariable Long id) {
        log.debug("REST request to get RbacDataAccess : {}", id);
        Mono<RbacDataAccess> rbacDataAccess = rbacDataAccessService.findOne(id);
        return ResponseUtil.wrapOrNotFound(rbacDataAccess);
    }

    /**
     * {@code DELETE  /rbac-data-accesses/:id} : delete the "id" rbacDataAccess.
     *
     * @param id the id of the rbacDataAccess to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/rbac-data-accesses/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteRbacDataAccess(@PathVariable Long id) {
        log.debug("REST request to delete RbacDataAccess : {}", id);
        return rbacDataAccessService
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
