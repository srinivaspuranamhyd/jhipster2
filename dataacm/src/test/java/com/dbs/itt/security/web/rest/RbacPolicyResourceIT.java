package com.dbs.itt.security.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.dbs.itt.security.IntegrationTest;
import com.dbs.itt.security.domain.RbacPolicy;
import com.dbs.itt.security.repository.RbacPolicyRepository;
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
 * Integration tests for the {@link RbacPolicyResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class RbacPolicyResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESC = "AAAAAAAAAA";
    private static final String UPDATED_DESC = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/rbac-policies";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private RbacPolicyRepository rbacPolicyRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private RbacPolicy rbacPolicy;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RbacPolicy createEntity(EntityManager em) {
        RbacPolicy rbacPolicy = new RbacPolicy().name(DEFAULT_NAME).desc(DEFAULT_DESC);
        return rbacPolicy;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RbacPolicy createUpdatedEntity(EntityManager em) {
        RbacPolicy rbacPolicy = new RbacPolicy().name(UPDATED_NAME).desc(UPDATED_DESC);
        return rbacPolicy;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(RbacPolicy.class).block();
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
        rbacPolicy = createEntity(em);
    }

    @Test
    void createRbacPolicy() throws Exception {
        int databaseSizeBeforeCreate = rbacPolicyRepository.findAll().collectList().block().size();
        // Create the RbacPolicy
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacPolicy))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the RbacPolicy in the database
        List<RbacPolicy> rbacPolicyList = rbacPolicyRepository.findAll().collectList().block();
        assertThat(rbacPolicyList).hasSize(databaseSizeBeforeCreate + 1);
        RbacPolicy testRbacPolicy = rbacPolicyList.get(rbacPolicyList.size() - 1);
        assertThat(testRbacPolicy.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testRbacPolicy.getDesc()).isEqualTo(DEFAULT_DESC);
    }

    @Test
    void createRbacPolicyWithExistingId() throws Exception {
        // Create the RbacPolicy with an existing ID
        rbacPolicy.setId(1L);

        int databaseSizeBeforeCreate = rbacPolicyRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacPolicy))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RbacPolicy in the database
        List<RbacPolicy> rbacPolicyList = rbacPolicyRepository.findAll().collectList().block();
        assertThat(rbacPolicyList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = rbacPolicyRepository.findAll().collectList().block().size();
        // set the field null
        rbacPolicy.setName(null);

        // Create the RbacPolicy, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacPolicy))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<RbacPolicy> rbacPolicyList = rbacPolicyRepository.findAll().collectList().block();
        assertThat(rbacPolicyList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkDescIsRequired() throws Exception {
        int databaseSizeBeforeTest = rbacPolicyRepository.findAll().collectList().block().size();
        // set the field null
        rbacPolicy.setDesc(null);

        // Create the RbacPolicy, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacPolicy))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<RbacPolicy> rbacPolicyList = rbacPolicyRepository.findAll().collectList().block();
        assertThat(rbacPolicyList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllRbacPolicies() {
        // Initialize the database
        rbacPolicyRepository.save(rbacPolicy).block();

        // Get all the rbacPolicyList
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
            .value(hasItem(rbacPolicy.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].desc")
            .value(hasItem(DEFAULT_DESC));
    }

    @Test
    void getRbacPolicy() {
        // Initialize the database
        rbacPolicyRepository.save(rbacPolicy).block();

        // Get the rbacPolicy
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, rbacPolicy.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(rbacPolicy.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.desc")
            .value(is(DEFAULT_DESC));
    }

    @Test
    void getNonExistingRbacPolicy() {
        // Get the rbacPolicy
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewRbacPolicy() throws Exception {
        // Initialize the database
        rbacPolicyRepository.save(rbacPolicy).block();

        int databaseSizeBeforeUpdate = rbacPolicyRepository.findAll().collectList().block().size();

        // Update the rbacPolicy
        RbacPolicy updatedRbacPolicy = rbacPolicyRepository.findById(rbacPolicy.getId()).block();
        updatedRbacPolicy.name(UPDATED_NAME).desc(UPDATED_DESC);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedRbacPolicy.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedRbacPolicy))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RbacPolicy in the database
        List<RbacPolicy> rbacPolicyList = rbacPolicyRepository.findAll().collectList().block();
        assertThat(rbacPolicyList).hasSize(databaseSizeBeforeUpdate);
        RbacPolicy testRbacPolicy = rbacPolicyList.get(rbacPolicyList.size() - 1);
        assertThat(testRbacPolicy.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testRbacPolicy.getDesc()).isEqualTo(UPDATED_DESC);
    }

    @Test
    void putNonExistingRbacPolicy() throws Exception {
        int databaseSizeBeforeUpdate = rbacPolicyRepository.findAll().collectList().block().size();
        rbacPolicy.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, rbacPolicy.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacPolicy))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RbacPolicy in the database
        List<RbacPolicy> rbacPolicyList = rbacPolicyRepository.findAll().collectList().block();
        assertThat(rbacPolicyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchRbacPolicy() throws Exception {
        int databaseSizeBeforeUpdate = rbacPolicyRepository.findAll().collectList().block().size();
        rbacPolicy.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacPolicy))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RbacPolicy in the database
        List<RbacPolicy> rbacPolicyList = rbacPolicyRepository.findAll().collectList().block();
        assertThat(rbacPolicyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamRbacPolicy() throws Exception {
        int databaseSizeBeforeUpdate = rbacPolicyRepository.findAll().collectList().block().size();
        rbacPolicy.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacPolicy))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the RbacPolicy in the database
        List<RbacPolicy> rbacPolicyList = rbacPolicyRepository.findAll().collectList().block();
        assertThat(rbacPolicyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateRbacPolicyWithPatch() throws Exception {
        // Initialize the database
        rbacPolicyRepository.save(rbacPolicy).block();

        int databaseSizeBeforeUpdate = rbacPolicyRepository.findAll().collectList().block().size();

        // Update the rbacPolicy using partial update
        RbacPolicy partialUpdatedRbacPolicy = new RbacPolicy();
        partialUpdatedRbacPolicy.setId(rbacPolicy.getId());

        partialUpdatedRbacPolicy.name(UPDATED_NAME).desc(UPDATED_DESC);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRbacPolicy.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedRbacPolicy))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RbacPolicy in the database
        List<RbacPolicy> rbacPolicyList = rbacPolicyRepository.findAll().collectList().block();
        assertThat(rbacPolicyList).hasSize(databaseSizeBeforeUpdate);
        RbacPolicy testRbacPolicy = rbacPolicyList.get(rbacPolicyList.size() - 1);
        assertThat(testRbacPolicy.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testRbacPolicy.getDesc()).isEqualTo(UPDATED_DESC);
    }

    @Test
    void fullUpdateRbacPolicyWithPatch() throws Exception {
        // Initialize the database
        rbacPolicyRepository.save(rbacPolicy).block();

        int databaseSizeBeforeUpdate = rbacPolicyRepository.findAll().collectList().block().size();

        // Update the rbacPolicy using partial update
        RbacPolicy partialUpdatedRbacPolicy = new RbacPolicy();
        partialUpdatedRbacPolicy.setId(rbacPolicy.getId());

        partialUpdatedRbacPolicy.name(UPDATED_NAME).desc(UPDATED_DESC);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRbacPolicy.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedRbacPolicy))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RbacPolicy in the database
        List<RbacPolicy> rbacPolicyList = rbacPolicyRepository.findAll().collectList().block();
        assertThat(rbacPolicyList).hasSize(databaseSizeBeforeUpdate);
        RbacPolicy testRbacPolicy = rbacPolicyList.get(rbacPolicyList.size() - 1);
        assertThat(testRbacPolicy.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testRbacPolicy.getDesc()).isEqualTo(UPDATED_DESC);
    }

    @Test
    void patchNonExistingRbacPolicy() throws Exception {
        int databaseSizeBeforeUpdate = rbacPolicyRepository.findAll().collectList().block().size();
        rbacPolicy.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, rbacPolicy.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacPolicy))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RbacPolicy in the database
        List<RbacPolicy> rbacPolicyList = rbacPolicyRepository.findAll().collectList().block();
        assertThat(rbacPolicyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchRbacPolicy() throws Exception {
        int databaseSizeBeforeUpdate = rbacPolicyRepository.findAll().collectList().block().size();
        rbacPolicy.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacPolicy))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RbacPolicy in the database
        List<RbacPolicy> rbacPolicyList = rbacPolicyRepository.findAll().collectList().block();
        assertThat(rbacPolicyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamRbacPolicy() throws Exception {
        int databaseSizeBeforeUpdate = rbacPolicyRepository.findAll().collectList().block().size();
        rbacPolicy.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacPolicy))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the RbacPolicy in the database
        List<RbacPolicy> rbacPolicyList = rbacPolicyRepository.findAll().collectList().block();
        assertThat(rbacPolicyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteRbacPolicy() {
        // Initialize the database
        rbacPolicyRepository.save(rbacPolicy).block();

        int databaseSizeBeforeDelete = rbacPolicyRepository.findAll().collectList().block().size();

        // Delete the rbacPolicy
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, rbacPolicy.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<RbacPolicy> rbacPolicyList = rbacPolicyRepository.findAll().collectList().block();
        assertThat(rbacPolicyList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
