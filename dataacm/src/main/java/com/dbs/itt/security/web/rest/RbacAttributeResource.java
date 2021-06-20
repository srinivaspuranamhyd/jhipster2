package com.dbs.itt.security.web.rest;

import com.dbs.itt.security.domain.RbacAttribute;
import com.dbs.itt.security.repository.RbacAttributeRepository;
import com.dbs.itt.security.service.RbacAttributeService;
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
 * REST controller for managing {@link com.dbs.itt.security.domain.RbacAttribute}.
 */
@RestController
@RequestMapping("/api")
public class RbacAttributeResource {

    private final Logger log = LoggerFactory.getLogger(RbacAttributeResource.class);

    private static final String ENTITY_NAME = "dataacmRbacAttribute";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RbacAttributeService rbacAttributeService;

    private final RbacAttributeRepository rbacAttributeRepository;

    public RbacAttributeResource(RbacAttributeService rbacAttributeService, RbacAttributeRepository rbacAttributeRepository) {
        this.rbacAttributeService = rbacAttributeService;
        this.rbacAttributeRepository = rbacAttributeRepository;
    }

    /**
     * {@code POST  /rbac-attributes} : Create a new rbacAttribute.
     *
     * @param rbacAttribute the rbacAttribute to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new rbacAttribute, or with status {@code 400 (Bad Request)} if the rbacAttribute has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/rbac-attributes")
    public Mono<ResponseEntity<RbacAttribute>> createRbacAttribute(@Valid @RequestBody RbacAttribute rbacAttribute)
        throws URISyntaxException {
        log.debug("REST request to save RbacAttribute : {}", rbacAttribute);
        if (rbacAttribute.getId() != null) {
            throw new BadRequestAlertException("A new rbacAttribute cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return rbacAttributeService
            .save(rbacAttribute)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/rbac-attributes/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /rbac-attributes/:id} : Updates an existing rbacAttribute.
     *
     * @param id the id of the rbacAttribute to save.
     * @param rbacAttribute the rbacAttribute to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rbacAttribute,
     * or with status {@code 400 (Bad Request)} if the rbacAttribute is not valid,
     * or with status {@code 500 (Internal Server Error)} if the rbacAttribute couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/rbac-attributes/{id}")
    public Mono<ResponseEntity<RbacAttribute>> updateRbacAttribute(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody RbacAttribute rbacAttribute
    ) throws URISyntaxException {
        log.debug("REST request to update RbacAttribute : {}, {}", id, rbacAttribute);
        if (rbacAttribute.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, rbacAttribute.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return rbacAttributeRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return rbacAttributeService
                        .save(rbacAttribute)
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
     * {@code PATCH  /rbac-attributes/:id} : Partial updates given fields of an existing rbacAttribute, field will ignore if it is null
     *
     * @param id the id of the rbacAttribute to save.
     * @param rbacAttribute the rbacAttribute to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rbacAttribute,
     * or with status {@code 400 (Bad Request)} if the rbacAttribute is not valid,
     * or with status {@code 404 (Not Found)} if the rbacAttribute is not found,
     * or with status {@code 500 (Internal Server Error)} if the rbacAttribute couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/rbac-attributes/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<RbacAttribute>> partialUpdateRbacAttribute(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody RbacAttribute rbacAttribute
    ) throws URISyntaxException {
        log.debug("REST request to partial update RbacAttribute partially : {}, {}", id, rbacAttribute);
        if (rbacAttribute.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, rbacAttribute.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return rbacAttributeRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<RbacAttribute> result = rbacAttributeService.partialUpdate(rbacAttribute);

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
     * {@code GET  /rbac-attributes} : get all the rbacAttributes.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of rbacAttributes in body.
     */
    @GetMapping("/rbac-attributes")
    public Mono<ResponseEntity<List<RbacAttribute>>> getAllRbacAttributes(Pageable pageable, ServerHttpRequest request) {
        log.debug("REST request to get a page of RbacAttributes");
        return rbacAttributeService
            .countAll()
            .zipWith(rbacAttributeService.findAll(pageable).collectList())
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
     * {@code GET  /rbac-attributes/:id} : get the "id" rbacAttribute.
     *
     * @param id the id of the rbacAttribute to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the rbacAttribute, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/rbac-attributes/{id}")
    public Mono<ResponseEntity<RbacAttribute>> getRbacAttribute(@PathVariable Long id) {
        log.debug("REST request to get RbacAttribute : {}", id);
        Mono<RbacAttribute> rbacAttribute = rbacAttributeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(rbacAttribute);
    }

    /**
     * {@code DELETE  /rbac-attributes/:id} : delete the "id" rbacAttribute.
     *
     * @param id the id of the rbacAttribute to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/rbac-attributes/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteRbacAttribute(@PathVariable Long id) {
        log.debug("REST request to delete RbacAttribute : {}", id);
        return rbacAttributeService
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
