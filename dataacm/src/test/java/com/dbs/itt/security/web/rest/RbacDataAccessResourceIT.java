package com.dbs.itt.security.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.dbs.itt.security.IntegrationTest;
import com.dbs.itt.security.domain.RbacDataAccess;
import com.dbs.itt.security.repository.RbacDataAccessRepository;
import com.dbs.itt.security.service.EntityManager;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link RbacDataAccessResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class RbacDataAccessResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESC = "AAAAAAAAAA";
    private static final String UPDATED_DESC = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/rbac-data-accesses";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private RbacDataAccessRepository rbacDataAccessRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private RbacDataAccess rbacDataAccess;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RbacDataAccess createEntity(EntityManager em) {
        RbacDataAccess rbacDataAccess = new RbacDataAccess().name(DEFAULT_NAME).desc(DEFAULT_DESC);
        return rbacDataAccess;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RbacDataAccess createUpdatedEntity(EntityManager em) {
        RbacDataAccess rbacDataAccess = new RbacDataAccess().name(UPDATED_NAME).desc(UPDATED_DESC);
        return rbacDataAccess;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(RbacDataAccess.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void setupCsrf() {
        webTestClient = webTestClient.mutateWith(csrf());
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        rbacDataAccess = createEntity(em);
    }

    @Test
    void createRbacDataAccess() throws Exception {
        int databaseSizeBeforeCreate = rbacDataAccessRepository.findAll().collectList().block().size();
        // Create the RbacDataAccess
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacDataAccess))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the RbacDataAccess in the database
        List<RbacDataAccess> rbacDataAccessList = rbacDataAccessRepository.findAll().collectList().block();
        assertThat(rbacDataAccessList).hasSize(databaseSizeBeforeCreate + 1);
        RbacDataAccess testRbacDataAccess = rbacDataAccessList.get(rbacDataAccessList.size() - 1);
        assertThat(testRbacDataAccess.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testRbacDataAccess.getDesc()).isEqualTo(DEFAULT_DESC);
    }

    @Test
    void createRbacDataAccessWithExistingId() throws Exception {
        // Create the RbacDataAccess with an existing ID
        rbacDataAccess.setId(1L);

        int databaseSizeBeforeCreate = rbacDataAccessRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacDataAccess))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RbacDataAccess in the database
        List<RbacDataAccess> rbacDataAccessList = rbacDataAccessRepository.findAll().collectList().block();
        assertThat(rbacDataAccessList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = rbacDataAccessRepository.findAll().collectList().block().size();
        // set the field null
        rbacDataAccess.setName(null);

        // Create the RbacDataAccess, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacDataAccess))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<RbacDataAccess> rbacDataAccessList = rbacDataAccessRepository.findAll().collectList().block();
        assertThat(rbacDataAccessList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkDescIsRequired() throws Exception {
        int databaseSizeBeforeTest = rbacDataAccessRepository.findAll().collectList().block().size();
        // set the field null
        rbacDataAccess.setDesc(null);

        // Create the RbacDataAccess, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacDataAccess))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<RbacDataAccess> rbacDataAccessList = rbacDataAccessRepository.findAll().collectList().block();
        assertThat(rbacDataAccessList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllRbacDataAccesses() {
        // Initialize the database
        rbacDataAccessRepository.save(rbacDataAccess).block();

        // Get all the rbacDataAccessList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(rbacDataAccess.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].desc")
            .value(hasItem(DEFAULT_DESC));
    }

    @Test
    void getRbacDataAccess() {
        // Initialize the database
        rbacDataAccessRepository.save(rbacDataAccess).block();

        // Get the rbacDataAccess
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, rbacDataAccess.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(rbacDataAccess.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.desc")
            .value(is(DEFAULT_DESC));
    }

    @Test
    void getNonExistingRbacDataAccess() {
        // Get the rbacDataAccess
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewRbacDataAccess() throws Exception {
        // Initialize the database
        rbacDataAccessRepository.save(rbacDataAccess).block();

        int databaseSizeBeforeUpdate = rbacDataAccessRepository.findAll().collectList().block().size();

        // Update the rbacDataAccess
        RbacDataAccess updatedRbacDataAccess = rbacDataAccessRepository.findById(rbacDataAccess.getId()).block();
        updatedRbacDataAccess.name(UPDATED_NAME).desc(UPDATED_DESC);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedRbacDataAccess.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedRbacDataAccess))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RbacDataAccess in the database
        List<RbacDataAccess> rbacDataAccessList = rbacDataAccessRepository.findAll().collectList().block();
        assertThat(rbacDataAccessList).hasSize(databaseSizeBeforeUpdate);
        RbacDataAccess testRbacDataAccess = rbacDataAccessList.get(rbacDataAccessList.size() - 1);
        assertThat(testRbacDataAccess.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testRbacDataAccess.getDesc()).isEqualTo(UPDATED_DESC);
    }

    @Test
    void putNonExistingRbacDataAccess() throws Exception {
        int databaseSizeBeforeUpdate = rbacDataAccessRepository.findAll().collectList().block().size();
        rbacDataAccess.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, rbacDataAccess.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacDataAccess))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RbacDataAccess in the database
        List<RbacDataAccess> rbacDataAccessList = rbacDataAccessRepository.findAll().collectList().block();
        assertThat(rbacDataAccessList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchRbacDataAccess() throws Exception {
        int databaseSizeBeforeUpdate = rbacDataAccessRepository.findAll().collectList().block().size();
        rbacDataAccess.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacDataAccess))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RbacDataAccess in the database
        List<RbacDataAccess> rbacDataAccessList = rbacDataAccessRepository.findAll().collectList().block();
        assertThat(rbacDataAccessList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamRbacDataAccess() throws Exception {
        int databaseSizeBeforeUpdate = rbacDataAccessRepository.findAll().collectList().block().size();
        rbacDataAccess.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacDataAccess))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the RbacDataAccess in the database
        List<RbacDataAccess> rbacDataAccessList = rbacDataAccessRepository.findAll().collectList().block();
        assertThat(rbacDataAccessList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateRbacDataAccessWithPatch() throws Exception {
        // Initialize the database
        rbacDataAccessRepository.save(rbacDataAccess).block();

        int databaseSizeBeforeUpdate = rbacDataAccessRepository.findAll().collectList().block().size();

        // Update the rbacDataAccess using partial update
        RbacDataAccess partialUpdatedRbacDataAccess = new RbacDataAccess();
        partialUpdatedRbacDataAccess.setId(rbacDataAccess.getId());

        partialUpdatedRbacDataAccess.desc(UPDATED_DESC);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRbacDataAccess.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedRbacDataAccess))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RbacDataAccess in the database
        List<RbacDataAccess> rbacDataAccessList = rbacDataAccessRepository.findAll().collectList().block();
        assertThat(rbacDataAccessList).hasSize(databaseSizeBeforeUpdate);
        RbacDataAccess testRbacDataAccess = rbacDataAccessList.get(rbacDataAccessList.size() - 1);
        assertThat(testRbacDataAccess.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testRbacDataAccess.getDesc()).isEqualTo(UPDATED_DESC);
    }

    @Test
    void fullUpdateRbacDataAccessWithPatch() throws Exception {
        // Initialize the database
        rbacDataAccessRepository.save(rbacDataAccess).block();

        int databaseSizeBeforeUpdate = rbacDataAccessRepository.findAll().collectList().block().size();

        // Update the rbacDataAccess using partial update
        RbacDataAccess partialUpdatedRbacDataAccess = new RbacDataAccess();
        partialUpdatedRbacDataAccess.setId(rbacDataAccess.getId());

        partialUpdatedRbacDataAccess.name(UPDATED_NAME).desc(UPDATED_DESC);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRbacDataAccess.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedRbacDataAccess))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RbacDataAccess in the database
        List<RbacDataAccess> rbacDataAccessList = rbacDataAccessRepository.findAll().collectList().block();
        assertThat(rbacDataAccessList).hasSize(databaseSizeBeforeUpdate);
        RbacDataAccess testRbacDataAccess = rbacDataAccessList.get(rbacDataAccessList.size() - 1);
        assertThat(testRbacDataAccess.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testRbacDataAccess.getDesc()).isEqualTo(UPDATED_DESC);
    }

    @Test
    void patchNonExistingRbacDataAccess() throws Exception {
        int databaseSizeBeforeUpdate = rbacDataAccessRepository.findAll().collectList().block().size();
        rbacDataAccess.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, rbacDataAccess.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacDataAccess))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RbacDataAccess in the database
        List<RbacDataAccess> rbacDataAccessList = rbacDataAccessRepository.findAll().collectList().block();
        assertThat(rbacDataAccessList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchRbacDataAccess() throws Exception {
        int databaseSizeBeforeUpdate = rbacDataAccessRepository.findAll().collectList().block().size();
        rbacDataAccess.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacDataAccess))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RbacDataAccess in the database
        List<RbacDataAccess> rbacDataAccessList = rbacDataAccessRepository.findAll().collectList().block();
        assertThat(rbacDataAccessList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamRbacDataAccess() throws Exception {
        int databaseSizeBeforeUpdate = rbacDataAccessRepository.findAll().collectList().block().size();
        rbacDataAccess.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacDataAccess))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the RbacDataAccess in the database
        List<RbacDataAccess> rbacDataAccessList = rbacDataAccessRepository.findAll().collectList().block();
        assertThat(rbacDataAccessList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteRbacDataAccess() {
        // Initialize the database
        rbacDataAccessRepository.save(rbacDataAccess).block();

        int databaseSizeBeforeDelete = rbacDataAccessRepository.findAll().collectList().block().size();

        // Delete the rbacDataAccess
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, rbacDataAccess.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<RbacDataAccess> rbacDataAccessList = rbacDataAccessRepository.findAll().collectList().block();
        assertThat(rbacDataAccessList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
