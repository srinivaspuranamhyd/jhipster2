package com.dbs.itt.security.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.dbs.itt.security.IntegrationTest;
import com.dbs.itt.security.domain.RbacPermission;
import com.dbs.itt.security.repository.RbacPermissionRepository;
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
 * Integration tests for the {@link RbacPermissionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class RbacPermissionResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESC = "AAAAAAAAAA";
    private static final String UPDATED_DESC = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/rbac-permissions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private RbacPermissionRepository rbacPermissionRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private RbacPermission rbacPermission;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RbacPermission createEntity(EntityManager em) {
        RbacPermission rbacPermission = new RbacPermission().name(DEFAULT_NAME).desc(DEFAULT_DESC);
        return rbacPermission;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RbacPermission createUpdatedEntity(EntityManager em) {
        RbacPermission rbacPermission = new RbacPermission().name(UPDATED_NAME).desc(UPDATED_DESC);
        return rbacPermission;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(RbacPermission.class).block();
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
        rbacPermission = createEntity(em);
    }

    @Test
    void createRbacPermission() throws Exception {
        int databaseSizeBeforeCreate = rbacPermissionRepository.findAll().collectList().block().size();
        // Create the RbacPermission
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacPermission))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the RbacPermission in the database
        List<RbacPermission> rbacPermissionList = rbacPermissionRepository.findAll().collectList().block();
        assertThat(rbacPermissionList).hasSize(databaseSizeBeforeCreate + 1);
        RbacPermission testRbacPermission = rbacPermissionList.get(rbacPermissionList.size() - 1);
        assertThat(testRbacPermission.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testRbacPermission.getDesc()).isEqualTo(DEFAULT_DESC);
    }

    @Test
    void createRbacPermissionWithExistingId() throws Exception {
        // Create the RbacPermission with an existing ID
        rbacPermission.setId(1L);

        int databaseSizeBeforeCreate = rbacPermissionRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacPermission))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RbacPermission in the database
        List<RbacPermission> rbacPermissionList = rbacPermissionRepository.findAll().collectList().block();
        assertThat(rbacPermissionList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = rbacPermissionRepository.findAll().collectList().block().size();
        // set the field null
        rbacPermission.setName(null);

        // Create the RbacPermission, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacPermission))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<RbacPermission> rbacPermissionList = rbacPermissionRepository.findAll().collectList().block();
        assertThat(rbacPermissionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkDescIsRequired() throws Exception {
        int databaseSizeBeforeTest = rbacPermissionRepository.findAll().collectList().block().size();
        // set the field null
        rbacPermission.setDesc(null);

        // Create the RbacPermission, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacPermission))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<RbacPermission> rbacPermissionList = rbacPermissionRepository.findAll().collectList().block();
        assertThat(rbacPermissionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllRbacPermissions() {
        // Initialize the database
        rbacPermissionRepository.save(rbacPermission).block();

        // Get all the rbacPermissionList
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
            .value(hasItem(rbacPermission.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].desc")
            .value(hasItem(DEFAULT_DESC));
    }

    @Test
    void getRbacPermission() {
        // Initialize the database
        rbacPermissionRepository.save(rbacPermission).block();

        // Get the rbacPermission
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, rbacPermission.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(rbacPermission.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.desc")
            .value(is(DEFAULT_DESC));
    }

    @Test
    void getNonExistingRbacPermission() {
        // Get the rbacPermission
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewRbacPermission() throws Exception {
        // Initialize the database
        rbacPermissionRepository.save(rbacPermission).block();

        int databaseSizeBeforeUpdate = rbacPermissionRepository.findAll().collectList().block().size();

        // Update the rbacPermission
        RbacPermission updatedRbacPermission = rbacPermissionRepository.findById(rbacPermission.getId()).block();
        updatedRbacPermission.name(UPDATED_NAME).desc(UPDATED_DESC);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedRbacPermission.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedRbacPermission))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RbacPermission in the database
        List<RbacPermission> rbacPermissionList = rbacPermissionRepository.findAll().collectList().block();
        assertThat(rbacPermissionList).hasSize(databaseSizeBeforeUpdate);
        RbacPermission testRbacPermission = rbacPermissionList.get(rbacPermissionList.size() - 1);
        assertThat(testRbacPermission.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testRbacPermission.getDesc()).isEqualTo(UPDATED_DESC);
    }

    @Test
    void putNonExistingRbacPermission() throws Exception {
        int databaseSizeBeforeUpdate = rbacPermissionRepository.findAll().collectList().block().size();
        rbacPermission.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, rbacPermission.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacPermission))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RbacPermission in the database
        List<RbacPermission> rbacPermissionList = rbacPermissionRepository.findAll().collectList().block();
        assertThat(rbacPermissionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchRbacPermission() throws Exception {
        int databaseSizeBeforeUpdate = rbacPermissionRepository.findAll().collectList().block().size();
        rbacPermission.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacPermission))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RbacPermission in the database
        List<RbacPermission> rbacPermissionList = rbacPermissionRepository.findAll().collectList().block();
        assertThat(rbacPermissionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamRbacPermission() throws Exception {
        int databaseSizeBeforeUpdate = rbacPermissionRepository.findAll().collectList().block().size();
        rbacPermission.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacPermission))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the RbacPermission in the database
        List<RbacPermission> rbacPermissionList = rbacPermissionRepository.findAll().collectList().block();
        assertThat(rbacPermissionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateRbacPermissionWithPatch() throws Exception {
        // Initialize the database
        rbacPermissionRepository.save(rbacPermission).block();

        int databaseSizeBeforeUpdate = rbacPermissionRepository.findAll().collectList().block().size();

        // Update the rbacPermission using partial update
        RbacPermission partialUpdatedRbacPermission = new RbacPermission();
        partialUpdatedRbacPermission.setId(rbacPermission.getId());

        partialUpdatedRbacPermission.name(UPDATED_NAME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRbacPermission.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedRbacPermission))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RbacPermission in the database
        List<RbacPermission> rbacPermissionList = rbacPermissionRepository.findAll().collectList().block();
        assertThat(rbacPermissionList).hasSize(databaseSizeBeforeUpdate);
        RbacPermission testRbacPermission = rbacPermissionList.get(rbacPermissionList.size() - 1);
        assertThat(testRbacPermission.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testRbacPermission.getDesc()).isEqualTo(DEFAULT_DESC);
    }

    @Test
    void fullUpdateRbacPermissionWithPatch() throws Exception {
        // Initialize the database
        rbacPermissionRepository.save(rbacPermission).block();

        int databaseSizeBeforeUpdate = rbacPermissionRepository.findAll().collectList().block().size();

        // Update the rbacPermission using partial update
        RbacPermission partialUpdatedRbacPermission = new RbacPermission();
        partialUpdatedRbacPermission.setId(rbacPermission.getId());

        partialUpdatedRbacPermission.name(UPDATED_NAME).desc(UPDATED_DESC);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRbacPermission.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedRbacPermission))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RbacPermission in the database
        List<RbacPermission> rbacPermissionList = rbacPermissionRepository.findAll().collectList().block();
        assertThat(rbacPermissionList).hasSize(databaseSizeBeforeUpdate);
        RbacPermission testRbacPermission = rbacPermissionList.get(rbacPermissionList.size() - 1);
        assertThat(testRbacPermission.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testRbacPermission.getDesc()).isEqualTo(UPDATED_DESC);
    }

    @Test
    void patchNonExistingRbacPermission() throws Exception {
        int databaseSizeBeforeUpdate = rbacPermissionRepository.findAll().collectList().block().size();
        rbacPermission.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, rbacPermission.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacPermission))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RbacPermission in the database
        List<RbacPermission> rbacPermissionList = rbacPermissionRepository.findAll().collectList().block();
        assertThat(rbacPermissionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchRbacPermission() throws Exception {
        int databaseSizeBeforeUpdate = rbacPermissionRepository.findAll().collectList().block().size();
        rbacPermission.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacPermission))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RbacPermission in the database
        List<RbacPermission> rbacPermissionList = rbacPermissionRepository.findAll().collectList().block();
        assertThat(rbacPermissionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamRbacPermission() throws Exception {
        int databaseSizeBeforeUpdate = rbacPermissionRepository.findAll().collectList().block().size();
        rbacPermission.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacPermission))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the RbacPermission in the database
        List<RbacPermission> rbacPermissionList = rbacPermissionRepository.findAll().collectList().block();
        assertThat(rbacPermissionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteRbacPermission() {
        // Initialize the database
        rbacPermissionRepository.save(rbacPermission).block();

        int databaseSizeBeforeDelete = rbacPermissionRepository.findAll().collectList().block().size();

        // Delete the rbacPermission
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, rbacPermission.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<RbacPermission> rbacPermissionList = rbacPermissionRepository.findAll().collectList().block();
        assertThat(rbacPermissionList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
