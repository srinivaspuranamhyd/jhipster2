package com.dbs.itt.security.web.rest;

import com.dbs.itt.security.domain.RbacPermissionApproval;
import com.dbs.itt.security.repository.RbacPermissionApprovalRepository;
import com.dbs.itt.security.service.RbacPermissionApprovalService;
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
 * REST controller for managing {@link com.dbs.itt.security.domain.RbacPermissionApproval}.
 */
@RestController
@RequestMapping("/api")
public class RbacPermissionApprovalResource {

    private final Logger log = LoggerFactory.getLogger(RbacPermissionApprovalResource.class);

    private static final String ENTITY_NAME = "dataacmRbacPermissionApproval";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RbacPermissionApprovalService rbacPermissionApprovalService;

    private final RbacPermissionApprovalRepository rbacPermissionApprovalRepository;

    public RbacPermissionApprovalResource(
        RbacPermissionApprovalService rbacPermissionApprovalService,
        RbacPermissionApprovalRepository rbacPermissionApprovalRepository
    ) {
        this.rbacPermissionApprovalService = rbacPermissionApprovalService;
        this.rbacPermissionApprovalRepository = rbacPermissionApprovalRepository;
    }

    /**
     * {@code POST  /rbac-permission-approvals} : Create a new rbacPermissionApproval.
     *
     * @param rbacPermissionApproval the rbacPermissionApproval to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new rbacPermissionApproval, or with status {@code 400 (Bad Request)} if the rbacPermissionApproval has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/rbac-permission-approvals")
    public Mono<ResponseEntity<RbacPermissionApproval>> createRbacPermissionApproval(
        @Valid @RequestBody RbacPermissionApproval rbacPermissionApproval
    ) throws URISyntaxException {
        log.debug("REST request to save RbacPermissionApproval : {}", rbacPermissionApproval);
        if (rbacPermissionApproval.getId() != null) {
            throw new BadRequestAlertException("A new rbacPermissionApproval cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return rbacPermissionApprovalService
            .save(rbacPermissionApproval)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/rbac-permission-approvals/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /rbac-permission-approvals/:id} : Updates an existing rbacPermissionApproval.
     *
     * @param id the id of the rbacPermissionApproval to save.
     * @param rbacPermissionApproval the rbacPermissionApproval to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rbacPermissionApproval,
     * or with status {@code 400 (Bad Request)} if the rbacPermissionApproval is not valid,
     * or with status {@code 500 (Internal Server Error)} if the rbacPermissionApproval couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/rbac-permission-approvals/{id}")
    public Mono<ResponseEntity<RbacPermissionApproval>> updateRbacPermissionApproval(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody RbacPermissionApproval rbacPermissionApproval
    ) throws URISyntaxException {
        log.debug("REST request to update RbacPermissionApproval : {}, {}", id, rbacPermissionApproval);
        if (rbacPermissionApproval.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, rbacPermissionApproval.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return rbacPermissionApprovalRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return rbacPermissionApprovalService
                        .save(rbacPermissionApproval)
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
     * {@code PATCH  /rbac-permission-approvals/:id} : Partial updates given fields of an existing rbacPermissionApproval, field will ignore if it is null
     *
     * @param id the id of the rbacPermissionApproval to save.
     * @param rbacPermissionApproval the rbacPermissionApproval to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rbacPermissionApproval,
     * or with status {@code 400 (Bad Request)} if the rbacPermissionApproval is not valid,
     * or with status {@code 404 (Not Found)} if the rbacPermissionApproval is not found,
     * or with status {@code 500 (Internal Server Error)} if the rbacPermissionApproval couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/rbac-permission-approvals/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<RbacPermissionApproval>> partialUpdateRbacPermissionApproval(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody RbacPermissionApproval rbacPermissionApproval
    ) throws URISyntaxException {
        log.debug("REST request to partial update RbacPermissionApproval partially : {}, {}", id, rbacPermissionApproval);
        if (rbacPermissionApproval.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, rbacPermissionApproval.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return rbacPermissionApprovalRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<RbacPermissionApproval> result = rbacPermissionApprovalService.partialUpdate(rbacPermissionApproval);

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
     * {@code GET  /rbac-permission-approvals} : get all the rbacPermissionApprovals.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of rbacPermissionApprovals in body.
     */
    @GetMapping("/rbac-permission-approvals")
    public Mono<ResponseEntity<List<RbacPermissionApproval>>> getAllRbacPermissionApprovals(
        Pageable pageable,
        ServerHttpRequest request,
        @RequestParam(required = false) String filter
    ) {
        if ("permission-is-null".equals(filter)) {
            log.debug("REST request to get all RbacPermissionApprovals where permission is null");
            return rbacPermissionApprovalService.findAllWherePermissionIsNull().collectList().map(ResponseEntity::ok);
        }
        log.debug("REST request to get a page of RbacPermissionApprovals");
        return rbacPermissionApprovalService
            .countAll()
            .zipWith(rbacPermissionApprovalService.findAll(pageable).collectList())
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
     * {@code GET  /rbac-permission-approvals/:id} : get the "id" rbacPermissionApproval.
     *
     * @param id the id of the rbacPermissionApproval to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the rbacPermissionApproval, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/rbac-permission-approvals/{id}")
    public Mono<ResponseEntity<RbacPermissionApproval>> getRbacPermissionApproval(@PathVariable Long id) {
        log.debug("REST request to get RbacPermissionApproval : {}", id);
        Mono<RbacPermissionApproval> rbacPermissionApproval = rbacPermissionApprovalService.findOne(id);
        return ResponseUtil.wrapOrNotFound(rbacPermissionApproval);
    }

    /**
     * {@code DELETE  /rbac-permission-approvals/:id} : delete the "id" rbacPermissionApproval.
     *
     * @param id the id of the rbacPermissionApproval to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/rbac-permission-approvals/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteRbacPermissionApproval(@PathVariable Long id) {
        log.debug("REST request to delete RbacPermissionApproval : {}", id);
        return rbacPermissionApprovalService
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
