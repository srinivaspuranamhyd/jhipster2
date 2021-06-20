package com.dbs.itt.security.web.rest;

import com.dbs.itt.security.domain.RbacSecurityGroup;
import com.dbs.itt.security.repository.RbacSecurityGroupRepository;
import com.dbs.itt.security.service.RbacSecurityGroupService;
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
 * REST controller for managing {@link com.dbs.itt.security.domain.RbacSecurityGroup}.
 */
@RestController
@RequestMapping("/api")
public class RbacSecurityGroupResource {

    private final Logger log = LoggerFactory.getLogger(RbacSecurityGroupResource.class);

    private static final String ENTITY_NAME = "dataacmRbacSecurityGroup";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RbacSecurityGroupService rbacSecurityGroupService;

    private final RbacSecurityGroupRepository rbacSecurityGroupRepository;

    public RbacSecurityGroupResource(
        RbacSecurityGroupService rbacSecurityGroupService,
        RbacSecurityGroupRepository rbacSecurityGroupRepository
    ) {
        this.rbacSecurityGroupService = rbacSecurityGroupService;
        this.rbacSecurityGroupRepository = rbacSecurityGroupRepository;
    }

    /**
     * {@code POST  /rbac-security-groups} : Create a new rbacSecurityGroup.
     *
     * @param rbacSecurityGroup the rbacSecurityGroup to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new rbacSecurityGroup, or with status {@code 400 (Bad Request)} if the rbacSecurityGroup has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/rbac-security-groups")
    public Mono<ResponseEntity<RbacSecurityGroup>> createRbacSecurityGroup(@Valid @RequestBody RbacSecurityGroup rbacSecurityGroup)
        throws URISyntaxException {
        log.debug("REST request to save RbacSecurityGroup : {}", rbacSecurityGroup);
        if (rbacSecurityGroup.getId() != null) {
            throw new BadRequestAlertException("A new rbacSecurityGroup cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return rbacSecurityGroupService
            .save(rbacSecurityGroup)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/rbac-security-groups/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /rbac-security-groups/:id} : Updates an existing rbacSecurityGroup.
     *
     * @param id the id of the rbacSecurityGroup to save.
     * @param rbacSecurityGroup the rbacSecurityGroup to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rbacSecurityGroup,
     * or with status {@code 400 (Bad Request)} if the rbacSecurityGroup is not valid,
     * or with status {@code 500 (Internal Server Error)} if the rbacSecurityGroup couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/rbac-security-groups/{id}")
    public Mono<ResponseEntity<RbacSecurityGroup>> updateRbacSecurityGroup(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody RbacSecurityGroup rbacSecurityGroup
    ) throws URISyntaxException {
        log.debug("REST request to update RbacSecurityGroup : {}, {}", id, rbacSecurityGroup);
        if (rbacSecurityGroup.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, rbacSecurityGroup.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return rbacSecurityGroupRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return rbacSecurityGroupService
                        .save(rbacSecurityGroup)
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
     * {@code PATCH  /rbac-security-groups/:id} : Partial updates given fields of an existing rbacSecurityGroup, field will ignore if it is null
     *
     * @param id the id of the rbacSecurityGroup to save.
     * @param rbacSecurityGroup the rbacSecurityGroup to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rbacSecurityGroup,
     * or with status {@code 400 (Bad Request)} if the rbacSecurityGroup is not valid,
     * or with status {@code 404 (Not Found)} if the rbacSecurityGroup is not found,
     * or with status {@code 500 (Internal Server Error)} if the rbacSecurityGroup couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/rbac-security-groups/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<RbacSecurityGroup>> partialUpdateRbacSecurityGroup(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody RbacSecurityGroup rbacSecurityGroup
    ) throws URISyntaxException {
        log.debug("REST request to partial update RbacSecurityGroup partially : {}, {}", id, rbacSecurityGroup);
        if (rbacSecurityGroup.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, rbacSecurityGroup.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return rbacSecurityGroupRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<RbacSecurityGroup> result = rbacSecurityGroupService.partialUpdate(rbacSecurityGroup);

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
     * {@code GET  /rbac-security-groups} : get all the rbacSecurityGroups.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of rbacSecurityGroups in body.
     */
    @GetMapping("/rbac-security-groups")
    public Mono<ResponseEntity<List<RbacSecurityGroup>>> getAllRbacSecurityGroups(
        Pageable pageable,
        ServerHttpRequest request,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get a page of RbacSecurityGroups");
        return rbacSecurityGroupService
            .countAll()
            .zipWith(rbacSecurityGroupService.findAll(pageable).collectList())
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
     * {@code GET  /rbac-security-groups/:id} : get the "id" rbacSecurityGroup.
     *
     * @param id the id of the rbacSecurityGroup to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the rbacSecurityGroup, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/rbac-security-groups/{id}")
    public Mono<ResponseEntity<RbacSecurityGroup>> getRbacSecurityGroup(@PathVariable Long id) {
        log.debug("REST request to get RbacSecurityGroup : {}", id);
        Mono<RbacSecurityGroup> rbacSecurityGroup = rbacSecurityGroupService.findOne(id);
        return ResponseUtil.wrapOrNotFound(rbacSecurityGroup);
    }

    /**
     * {@code DELETE  /rbac-security-groups/:id} : delete the "id" rbacSecurityGroup.
     *
     * @param id the id of the rbacSecurityGroup to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/rbac-security-groups/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteRbacSecurityGroup(@PathVariable Long id) {
        log.debug("REST request to delete RbacSecurityGroup : {}", id);
        return rbacSecurityGroupService
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
