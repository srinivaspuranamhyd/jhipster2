package com.dbs.itt.security.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.dbs.itt.security.IntegrationTest;
import com.dbs.itt.security.domain.RbacAttributeVal;
import com.dbs.itt.security.repository.RbacAttributeValRepository;
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
 * Integration tests for the {@link RbacAttributeValResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class RbacAttributeValResourceIT {

    private static final String DEFAULT_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_VALUE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/rbac-attribute-vals";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private RbacAttributeValRepository rbacAttributeValRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private RbacAttributeVal rbacAttributeVal;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RbacAttributeVal createEntity(EntityManager em) {
        RbacAttributeVal rbacAttributeVal = new RbacAttributeVal().value(DEFAULT_VALUE);
        return rbacAttributeVal;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RbacAttributeVal createUpdatedEntity(EntityManager em) {
        RbacAttributeVal rbacAttributeVal = new RbacAttributeVal().value(UPDATED_VALUE);
        return rbacAttributeVal;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(RbacAttributeVal.class).block();
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
        rbacAttributeVal = createEntity(em);
    }

    @Test
    void createRbacAttributeVal() throws Exception {
        int databaseSizeBeforeCreate = rbacAttributeValRepository.findAll().collectList().block().size();
        // Create the RbacAttributeVal
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacAttributeVal))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the RbacAttributeVal in the database
        List<RbacAttributeVal> rbacAttributeValList = rbacAttributeValRepository.findAll().collectList().block();
        assertThat(rbacAttributeValList).hasSize(databaseSizeBeforeCreate + 1);
        RbacAttributeVal testRbacAttributeVal = rbacAttributeValList.get(rbacAttributeValList.size() - 1);
        assertThat(testRbacAttributeVal.getValue()).isEqualTo(DEFAULT_VALUE);
    }

    @Test
    void createRbacAttributeValWithExistingId() throws Exception {
        // Create the RbacAttributeVal with an existing ID
        rbacAttributeVal.setId(1L);

        int databaseSizeBeforeCreate = rbacAttributeValRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacAttributeVal))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RbacAttributeVal in the database
        List<RbacAttributeVal> rbacAttributeValList = rbacAttributeValRepository.findAll().collectList().block();
        assertThat(rbacAttributeValList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkValueIsRequired() throws Exception {
        int databaseSizeBeforeTest = rbacAttributeValRepository.findAll().collectList().block().size();
        // set the field null
        rbacAttributeVal.setValue(null);

        // Create the RbacAttributeVal, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacAttributeVal))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<RbacAttributeVal> rbacAttributeValList = rbacAttributeValRepository.findAll().collectList().block();
        assertThat(rbacAttributeValList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllRbacAttributeVals() {
        // Initialize the database
        rbacAttributeValRepository.save(rbacAttributeVal).block();

        // Get all the rbacAttributeValList
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
            .value(hasItem(rbacAttributeVal.getId().intValue()))
            .jsonPath("$.[*].value")
            .value(hasItem(DEFAULT_VALUE));
    }

    @Test
    void getRbacAttributeVal() {
        // Initialize the database
        rbacAttributeValRepository.save(rbacAttributeVal).block();

        // Get the rbacAttributeVal
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, rbacAttributeVal.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(rbacAttributeVal.getId().intValue()))
            .jsonPath("$.value")
            .value(is(DEFAULT_VALUE));
    }

    @Test
    void getNonExistingRbacAttributeVal() {
        // Get the rbacAttributeVal
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewRbacAttributeVal() throws Exception {
        // Initialize the database
        rbacAttributeValRepository.save(rbacAttributeVal).block();

        int databaseSizeBeforeUpdate = rbacAttributeValRepository.findAll().collectList().block().size();

        // Update the rbacAttributeVal
        RbacAttributeVal updatedRbacAttributeVal = rbacAttributeValRepository.findById(rbacAttributeVal.getId()).block();
        updatedRbacAttributeVal.value(UPDATED_VALUE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedRbacAttributeVal.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedRbacAttributeVal))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RbacAttributeVal in the database
        List<RbacAttributeVal> rbacAttributeValList = rbacAttributeValRepository.findAll().collectList().block();
        assertThat(rbacAttributeValList).hasSize(databaseSizeBeforeUpdate);
        RbacAttributeVal testRbacAttributeVal = rbacAttributeValList.get(rbacAttributeValList.size() - 1);
        assertThat(testRbacAttributeVal.getValue()).isEqualTo(UPDATED_VALUE);
    }

    @Test
    void putNonExistingRbacAttributeVal() throws Exception {
        int databaseSizeBeforeUpdate = rbacAttributeValRepository.findAll().collectList().block().size();
        rbacAttributeVal.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, rbacAttributeVal.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacAttributeVal))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RbacAttributeVal in the database
        List<RbacAttributeVal> rbacAttributeValList = rbacAttributeValRepository.findAll().collectList().block();
        assertThat(rbacAttributeValList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchRbacAttributeVal() throws Exception {
        int databaseSizeBeforeUpdate = rbacAttributeValRepository.findAll().collectList().block().size();
        rbacAttributeVal.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacAttributeVal))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RbacAttributeVal in the database
        List<RbacAttributeVal> rbacAttributeValList = rbacAttributeValRepository.findAll().collectList().block();
        assertThat(rbacAttributeValList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamRbacAttributeVal() throws Exception {
        int databaseSizeBeforeUpdate = rbacAttributeValRepository.findAll().collectList().block().size();
        rbacAttributeVal.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacAttributeVal))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the RbacAttributeVal in the database
        List<RbacAttributeVal> rbacAttributeValList = rbacAttributeValRepository.findAll().collectList().block();
        assertThat(rbacAttributeValList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateRbacAttributeValWithPatch() throws Exception {
        // Initialize the database
        rbacAttributeValRepository.save(rbacAttributeVal).block();

        int databaseSizeBeforeUpdate = rbacAttributeValRepository.findAll().collectList().block().size();

        // Update the rbacAttributeVal using partial update
        RbacAttributeVal partialUpdatedRbacAttributeVal = new RbacAttributeVal();
        partialUpdatedRbacAttributeVal.setId(rbacAttributeVal.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRbacAttributeVal.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedRbacAttributeVal))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RbacAttributeVal in the database
        List<RbacAttributeVal> rbacAttributeValList = rbacAttributeValRepository.findAll().collectList().block();
        assertThat(rbacAttributeValList).hasSize(databaseSizeBeforeUpdate);
        RbacAttributeVal testRbacAttributeVal = rbacAttributeValList.get(rbacAttributeValList.size() - 1);
        assertThat(testRbacAttributeVal.getValue()).isEqualTo(DEFAULT_VALUE);
    }

    @Test
    void fullUpdateRbacAttributeValWithPatch() throws Exception {
        // Initialize the database
        rbacAttributeValRepository.save(rbacAttributeVal).block();

        int databaseSizeBeforeUpdate = rbacAttributeValRepository.findAll().collectList().block().size();

        // Update the rbacAttributeVal using partial update
        RbacAttributeVal partialUpdatedRbacAttributeVal = new RbacAttributeVal();
        partialUpdatedRbacAttributeVal.setId(rbacAttributeVal.getId());

        partialUpdatedRbacAttributeVal.value(UPDATED_VALUE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRbacAttributeVal.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedRbacAttributeVal))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RbacAttributeVal in the database
        List<RbacAttributeVal> rbacAttributeValList = rbacAttributeValRepository.findAll().collectList().block();
        assertThat(rbacAttributeValList).hasSize(databaseSizeBeforeUpdate);
        RbacAttributeVal testRbacAttributeVal = rbacAttributeValList.get(rbacAttributeValList.size() - 1);
        assertThat(testRbacAttributeVal.getValue()).isEqualTo(UPDATED_VALUE);
    }

    @Test
    void patchNonExistingRbacAttributeVal() throws Exception {
        int databaseSizeBeforeUpdate = rbacAttributeValRepository.findAll().collectList().block().size();
        rbacAttributeVal.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, rbacAttributeVal.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacAttributeVal))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RbacAttributeVal in the database
        List<RbacAttributeVal> rbacAttributeValList = rbacAttributeValRepository.findAll().collectList().block();
        assertThat(rbacAttributeValList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchRbacAttributeVal() throws Exception {
        int databaseSizeBeforeUpdate = rbacAttributeValRepository.findAll().collectList().block().size();
        rbacAttributeVal.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacAttributeVal))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RbacAttributeVal in the database
        List<RbacAttributeVal> rbacAttributeValList = rbacAttributeValRepository.findAll().collectList().block();
        assertThat(rbacAttributeValList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamRbacAttributeVal() throws Exception {
        int databaseSizeBeforeUpdate = rbacAttributeValRepository.findAll().collectList().block().size();
        rbacAttributeVal.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacAttributeVal))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the RbacAttributeVal in the database
        List<RbacAttributeVal> rbacAttributeValList = rbacAttributeValRepository.findAll().collectList().block();
        assertThat(rbacAttributeValList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteRbacAttributeVal() {
        // Initialize the database
        rbacAttributeValRepository.save(rbacAttributeVal).block();

        int databaseSizeBeforeDelete = rbacAttributeValRepository.findAll().collectList().block().size();

        // Delete the rbacAttributeVal
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, rbacAttributeVal.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<RbacAttributeVal> rbacAttributeValList = rbacAttributeValRepository.findAll().collectList().block();
        assertThat(rbacAttributeValList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
