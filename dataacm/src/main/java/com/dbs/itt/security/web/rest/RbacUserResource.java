package com.dbs.itt.security.web.rest;

import com.dbs.itt.security.domain.RbacUser;
import com.dbs.itt.security.repository.RbacUserRepository;
import com.dbs.itt.security.service.RbacUserService;
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
 * REST controller for managing {@link com.dbs.itt.security.domain.RbacUser}.
 */
@RestController
@RequestMapping("/api")
public class RbacUserResource {

    private final Logger log = LoggerFactory.getLogger(RbacUserResource.class);

    private static final String ENTITY_NAME = "dataacmRbacUser";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RbacUserService rbacUserService;

    private final RbacUserRepository rbacUserRepository;

    public RbacUserResource(RbacUserService rbacUserService, RbacUserRepository rbacUserRepository) {
        this.rbacUserService = rbacUserService;
        this.rbacUserRepository = rbacUserRepository;
    }

    /**
     * {@code POST  /rbac-users} : Create a new rbacUser.
     *
     * @param rbacUser the rbacUser to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new rbacUser, or with status {@code 400 (Bad Request)} if the rbacUser has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/rbac-users")
    public Mono<ResponseEntity<RbacUser>> createRbacUser(@Valid @RequestBody RbacUser rbacUser) throws URISyntaxException {
        log.debug("REST request to save RbacUser : {}", rbacUser);
        if (rbacUser.getId() != null) {
            throw new BadRequestAlertException("A new rbacUser cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return rbacUserService
            .save(rbacUser)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/rbac-users/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /rbac-users/:id} : Updates an existing rbacUser.
     *
     * @param id the id of the rbacUser to save.
     * @param rbacUser the rbacUser to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rbacUser,
     * or with status {@code 400 (Bad Request)} if the rbacUser is not valid,
     * or with status {@code 500 (Internal Server Error)} if the rbacUser couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/rbac-users/{id}")
    public Mono<ResponseEntity<RbacUser>> updateRbacUser(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody RbacUser rbacUser
    ) throws URISyntaxException {
        log.debug("REST request to update RbacUser : {}, {}", id, rbacUser);
        if (rbacUser.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, rbacUser.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return rbacUserRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return rbacUserService
                        .save(rbacUser)
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
     * {@code PATCH  /rbac-users/:id} : Partial updates given fields of an existing rbacUser, field will ignore if it is null
     *
     * @param id the id of the rbacUser to save.
     * @param rbacUser the rbacUser to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rbacUser,
     * or with status {@code 400 (Bad Request)} if the rbacUser is not valid,
     * or with status {@code 404 (Not Found)} if the rbacUser is not found,
     * or with status {@code 500 (Internal Server Error)} if the rbacUser couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/rbac-users/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<RbacUser>> partialUpdateRbacUser(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody RbacUser rbacUser
    ) throws URISyntaxException {
        log.debug("REST request to partial update RbacUser partially : {}, {}", id, rbacUser);
        if (rbacUser.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, rbacUser.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return rbacUserRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<RbacUser> result = rbacUserService.partialUpdate(rbacUser);

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
     * {@code GET  /rbac-users} : get all the rbacUsers.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of rbacUsers in body.
     */
    @GetMapping("/rbac-users")
    public Mono<ResponseEntity<List<RbacUser>>> getAllRbacUsers(
        Pageable pageable,
        ServerHttpRequest request,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get a page of RbacUsers");
        return rbacUserService
            .countAll()
            .zipWith(rbacUserService.findAll(pageable).collectList())
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
     * {@code GET  /rbac-users/:id} : get the "id" rbacUser.
     *
     * @param id the id of the rbacUser to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the rbacUser, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/rbac-users/{id}")
    public Mono<ResponseEntity<RbacUser>> getRbacUser(@PathVariable Long id) {
        log.debug("REST request to get RbacUser : {}", id);
        Mono<RbacUser> rbacUser = rbacUserService.findOne(id);
        return ResponseUtil.wrapOrNotFound(rbacUser);
    }

    /**
     * {@code DELETE  /rbac-users/:id} : delete the "id" rbacUser.
     *
     * @param id the id of the rbacUser to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/rbac-users/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteRbacUser(@PathVariable Long id) {
        log.debug("REST request to delete RbacUser : {}", id);
        return rbacUserService
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
