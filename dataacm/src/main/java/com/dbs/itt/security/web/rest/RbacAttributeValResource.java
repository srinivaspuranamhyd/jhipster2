package com.dbs.itt.security.web.rest;

import com.dbs.itt.security.domain.RbacAttributeVal;
import com.dbs.itt.security.repository.RbacAttributeValRepository;
import com.dbs.itt.security.service.RbacAttributeValService;
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
 * REST controller for managing {@link com.dbs.itt.security.domain.RbacAttributeVal}.
 */
@RestController
@RequestMapping("/api")
public class RbacAttributeValResource {

    private final Logger log = LoggerFactory.getLogger(RbacAttributeValResource.class);

    private static final String ENTITY_NAME = "dataacmRbacAttributeVal";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RbacAttributeValService rbacAttributeValService;

    private final RbacAttributeValRepository rbacAttributeValRepository;

    public RbacAttributeValResource(
        RbacAttributeValService rbacAttributeValService,
        RbacAttributeValRepository rbacAttributeValRepository
    ) {
        this.rbacAttributeValService = rbacAttributeValService;
        this.rbacAttributeValRepository = rbacAttributeValRepository;
    }

    /**
     * {@code POST  /rbac-attribute-vals} : Create a new rbacAttributeVal.
     *
     * @param rbacAttributeVal the rbacAttributeVal to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new rbacAttributeVal, or with status {@code 400 (Bad Request)} if the rbacAttributeVal has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/rbac-attribute-vals")
    public Mono<ResponseEntity<RbacAttributeVal>> createRbacAttributeVal(@Valid @RequestBody RbacAttributeVal rbacAttributeVal)
        throws URISyntaxException {
        log.debug("REST request to save RbacAttributeVal : {}", rbacAttributeVal);
        if (rbacAttributeVal.getId() != null) {
            throw new BadRequestAlertException("A new rbacAttributeVal cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return rbacAttributeValService
            .save(rbacAttributeVal)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/rbac-attribute-vals/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /rbac-attribute-vals/:id} : Updates an existing rbacAttributeVal.
     *
     * @param id the id of the rbacAttributeVal to save.
     * @param rbacAttributeVal the rbacAttributeVal to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rbacAttributeVal,
     * or with status {@code 400 (Bad Request)} if the rbacAttributeVal is not valid,
     * or with status {@code 500 (Internal Server Error)} if the rbacAttributeVal couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/rbac-attribute-vals/{id}")
    public Mono<ResponseEntity<RbacAttributeVal>> updateRbacAttributeVal(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody RbacAttributeVal rbacAttributeVal
    ) throws URISyntaxException {
        log.debug("REST request to update RbacAttributeVal : {}, {}", id, rbacAttributeVal);
        if (rbacAttributeVal.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, rbacAttributeVal.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return rbacAttributeValRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return rbacAttributeValService
                        .save(rbacAttributeVal)
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
     * {@code PATCH  /rbac-attribute-vals/:id} : Partial updates given fields of an existing rbacAttributeVal, field will ignore if it is null
     *
     * @param id the id of the rbacAttributeVal to save.
     * @param rbacAttributeVal the rbacAttributeVal to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rbacAttributeVal,
     * or with status {@code 400 (Bad Request)} if the rbacAttributeVal is not valid,
     * or with status {@code 404 (Not Found)} if the rbacAttributeVal is not found,
     * or with status {@code 500 (Internal Server Error)} if the rbacAttributeVal couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/rbac-attribute-vals/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<RbacAttributeVal>> partialUpdateRbacAttributeVal(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody RbacAttributeVal rbacAttributeVal
    ) throws URISyntaxException {
        log.debug("REST request to partial update RbacAttributeVal partially : {}, {}", id, rbacAttributeVal);
        if (rbacAttributeVal.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, rbacAttributeVal.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return rbacAttributeValRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<RbacAttributeVal> result = rbacAttributeValService.partialUpdate(rbacAttributeVal);

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
     * {@code GET  /rbac-attribute-vals} : get all the rbacAttributeVals.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of rbacAttributeVals in body.
     */
    @GetMapping("/rbac-attribute-vals")
    public Mono<ResponseEntity<List<RbacAttributeVal>>> getAllRbacAttributeVals(Pageable pageable, ServerHttpRequest request) {
        log.debug("REST request to get a page of RbacAttributeVals");
        return rbacAttributeValService
            .countAll()
            .zipWith(rbacAttributeValService.findAll(pageable).collectList())
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
     * {@code GET  /rbac-attribute-vals/:id} : get the "id" rbacAttributeVal.
     *
     * @param id the id of the rbacAttributeVal to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the rbacAttributeVal, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/rbac-attribute-vals/{id}")
    public Mono<ResponseEntity<RbacAttributeVal>> getRbacAttributeVal(@PathVariable Long id) {
        log.debug("REST request to get RbacAttributeVal : {}", id);
        Mono<RbacAttributeVal> rbacAttributeVal = rbacAttributeValService.findOne(id);
        return ResponseUtil.wrapOrNotFound(rbacAttributeVal);
    }

    /**
     * {@code DELETE  /rbac-attribute-vals/:id} : delete the "id" rbacAttributeVal.
     *
     * @param id the id of the rbacAttributeVal to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/rbac-attribute-vals/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteRbacAttributeVal(@PathVariable Long id) {
        log.debug("REST request to delete RbacAttributeVal : {}", id);
        return rbacAttributeValService
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
