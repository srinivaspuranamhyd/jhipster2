package com.dbs.itt.security.web.rest;

import com.dbs.itt.security.domain.RbacPolicy;
import com.dbs.itt.security.repository.RbacPolicyRepository;
import com.dbs.itt.security.service.RbacPolicyService;
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
 * REST controller for managing {@link com.dbs.itt.security.domain.RbacPolicy}.
 */
@RestController
@RequestMapping("/api")
public class RbacPolicyResource {

    private final Logger log = LoggerFactory.getLogger(RbacPolicyResource.class);

    private static final String ENTITY_NAME = "dataacmRbacPolicy";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RbacPolicyService rbacPolicyService;

    private final RbacPolicyRepository rbacPolicyRepository;

    public RbacPolicyResource(RbacPolicyService rbacPolicyService, RbacPolicyRepository rbacPolicyRepository) {
        this.rbacPolicyService = rbacPolicyService;
        this.rbacPolicyRepository = rbacPolicyRepository;
    }

    /**
     * {@code POST  /rbac-policies} : Create a new rbacPolicy.
     *
     * @param rbacPolicy the rbacPolicy to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new rbacPolicy, or with status {@code 400 (Bad Request)} if the rbacPolicy has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/rbac-policies")
    public Mono<ResponseEntity<RbacPolicy>> createRbacPolicy(@Valid @RequestBody RbacPolicy rbacPolicy) throws URISyntaxException {
        log.debug("REST request to save RbacPolicy : {}", rbacPolicy);
        if (rbacPolicy.getId() != null) {
            throw new BadRequestAlertException("A new rbacPolicy cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return rbacPolicyService
            .save(rbacPolicy)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/rbac-policies/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /rbac-policies/:id} : Updates an existing rbacPolicy.
     *
     * @param id the id of the rbacPolicy to save.
     * @param rbacPolicy the rbacPolicy to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rbacPolicy,
     * or with status {@code 400 (Bad Request)} if the rbacPolicy is not valid,
     * or with status {@code 500 (Internal Server Error)} if the rbacPolicy couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/rbac-policies/{id}")
    public Mono<ResponseEntity<RbacPolicy>> updateRbacPolicy(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody RbacPolicy rbacPolicy
    ) throws URISyntaxException {
        log.debug("REST request to update RbacPolicy : {}, {}", id, rbacPolicy);
        if (rbacPolicy.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, rbacPolicy.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return rbacPolicyRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return rbacPolicyService
                        .save(rbacPolicy)
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
     * {@code PATCH  /rbac-policies/:id} : Partial updates given fields of an existing rbacPolicy, field will ignore if it is null
     *
     * @param id the id of the rbacPolicy to save.
     * @param rbacPolicy the rbacPolicy to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rbacPolicy,
     * or with status {@code 400 (Bad Request)} if the rbacPolicy is not valid,
     * or with status {@code 404 (Not Found)} if the rbacPolicy is not found,
     * or with status {@code 500 (Internal Server Error)} if the rbacPolicy couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/rbac-policies/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<RbacPolicy>> partialUpdateRbacPolicy(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody RbacPolicy rbacPolicy
    ) throws URISyntaxException {
        log.debug("REST request to partial update RbacPolicy partially : {}, {}", id, rbacPolicy);
        if (rbacPolicy.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, rbacPolicy.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return rbacPolicyRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<RbacPolicy> result = rbacPolicyService.partialUpdate(rbacPolicy);

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
     * {@code GET  /rbac-policies} : get all the rbacPolicies.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of rbacPolicies in body.
     */
    @GetMapping("/rbac-policies")
    public Mono<ResponseEntity<List<RbacPolicy>>> getAllRbacPolicies(Pageable pageable, ServerHttpRequest request) {
        log.debug("REST request to get a page of RbacPolicies");
        return rbacPolicyService
            .countAll()
            .zipWith(rbacPolicyService.findAll(pageable).collectList())
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
     * {@code GET  /rbac-policies/:id} : get the "id" rbacPolicy.
     *
     * @param id the id of the rbacPolicy to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the rbacPolicy, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/rbac-policies/{id}")
    public Mono<ResponseEntity<RbacPolicy>> getRbacPolicy(@PathVariable Long id) {
        log.debug("REST request to get RbacPolicy : {}", id);
        Mono<RbacPolicy> rbacPolicy = rbacPolicyService.findOne(id);
        return ResponseUtil.wrapOrNotFound(rbacPolicy);
    }

    /**
     * {@code DELETE  /rbac-policies/:id} : delete the "id" rbacPolicy.
     *
     * @param id the id of the rbacPolicy to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/rbac-policies/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteRbacPolicy(@PathVariable Long id) {
        log.debug("REST request to delete RbacPolicy : {}", id);
        return rbacPolicyService
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
