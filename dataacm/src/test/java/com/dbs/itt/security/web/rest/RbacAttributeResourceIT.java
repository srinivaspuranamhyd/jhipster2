package com.dbs.itt.security.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.dbs.itt.security.IntegrationTest;
import com.dbs.itt.security.domain.RbacAttribute;
import com.dbs.itt.security.repository.RbacAttributeRepository;
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
 * Integration tests for the {@link RbacAttributeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class RbacAttributeResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/rbac-attributes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private RbacAttributeRepository rbacAttributeRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private RbacAttribute rbacAttribute;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RbacAttribute createEntity(EntityManager em) {
        RbacAttribute rbacAttribute = new RbacAttribute().name(DEFAULT_NAME).type(DEFAULT_TYPE);
        return rbacAttribute;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RbacAttribute createUpdatedEntity(EntityManager em) {
        RbacAttribute rbacAttribute = new RbacAttribute().name(UPDATED_NAME).type(UPDATED_TYPE);
        return rbacAttribute;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(RbacAttribute.class).block();
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
        rbacAttribute = createEntity(em);
    }

    @Test
    void createRbacAttribute() throws Exception {
        int databaseSizeBeforeCreate = rbacAttributeRepository.findAll().collectList().block().size();
        // Create the RbacAttribute
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacAttribute))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the RbacAttribute in the database
        List<RbacAttribute> rbacAttributeList = rbacAttributeRepository.findAll().collectList().block();
        assertThat(rbacAttributeList).hasSize(databaseSizeBeforeCreate + 1);
        RbacAttribute testRbacAttribute = rbacAttributeList.get(rbacAttributeList.size() - 1);
        assertThat(testRbacAttribute.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testRbacAttribute.getType()).isEqualTo(DEFAULT_TYPE);
    }

    @Test
    void createRbacAttributeWithExistingId() throws Exception {
        // Create the RbacAttribute with an existing ID
        rbacAttribute.setId(1L);

        int databaseSizeBeforeCreate = rbacAttributeRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacAttribute))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RbacAttribute in the database
        List<RbacAttribute> rbacAttributeList = rbacAttributeRepository.findAll().collectList().block();
        assertThat(rbacAttributeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = rbacAttributeRepository.findAll().collectList().block().size();
        // set the field null
        rbacAttribute.setName(null);

        // Create the RbacAttribute, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacAttribute))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<RbacAttribute> rbacAttributeList = rbacAttributeRepository.findAll().collectList().block();
        assertThat(rbacAttributeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = rbacAttributeRepository.findAll().collectList().block().size();
        // set the field null
        rbacAttribute.setType(null);

        // Create the RbacAttribute, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacAttribute))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<RbacAttribute> rbacAttributeList = rbacAttributeRepository.findAll().collectList().block();
        assertThat(rbacAttributeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllRbacAttributes() {
        // Initialize the database
        rbacAttributeRepository.save(rbacAttribute).block();

        // Get all the rbacAttributeList
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
            .value(hasItem(rbacAttribute.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].type")
            .value(hasItem(DEFAULT_TYPE));
    }

    @Test
    void getRbacAttribute() {
        // Initialize the database
        rbacAttributeRepository.save(rbacAttribute).block();

        // Get the rbacAttribute
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, rbacAttribute.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(rbacAttribute.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.type")
            .value(is(DEFAULT_TYPE));
    }

    @Test
    void getNonExistingRbacAttribute() {
        // Get the rbacAttribute
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewRbacAttribute() throws Exception {
        // Initialize the database
        rbacAttributeRepository.save(rbacAttribute).block();

        int databaseSizeBeforeUpdate = rbacAttributeRepository.findAll().collectList().block().size();

        // Update the rbacAttribute
        RbacAttribute updatedRbacAttribute = rbacAttributeRepository.findById(rbacAttribute.getId()).block();
        updatedRbacAttribute.name(UPDATED_NAME).type(UPDATED_TYPE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedRbacAttribute.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedRbacAttribute))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RbacAttribute in the database
        List<RbacAttribute> rbacAttributeList = rbacAttributeRepository.findAll().collectList().block();
        assertThat(rbacAttributeList).hasSize(databaseSizeBeforeUpdate);
        RbacAttribute testRbacAttribute = rbacAttributeList.get(rbacAttributeList.size() - 1);
        assertThat(testRbacAttribute.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testRbacAttribute.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    void putNonExistingRbacAttribute() throws Exception {
        int databaseSizeBeforeUpdate = rbacAttributeRepository.findAll().collectList().block().size();
        rbacAttribute.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, rbacAttribute.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacAttribute))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RbacAttribute in the database
        List<RbacAttribute> rbacAttributeList = rbacAttributeRepository.findAll().collectList().block();
        assertThat(rbacAttributeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchRbacAttribute() throws Exception {
        int databaseSizeBeforeUpdate = rbacAttributeRepository.findAll().collectList().block().size();
        rbacAttribute.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacAttribute))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RbacAttribute in the database
        List<RbacAttribute> rbacAttributeList = rbacAttributeRepository.findAll().collectList().block();
        assertThat(rbacAttributeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamRbacAttribute() throws Exception {
        int databaseSizeBeforeUpdate = rbacAttributeRepository.findAll().collectList().block().size();
        rbacAttribute.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacAttribute))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the RbacAttribute in the database
        List<RbacAttribute> rbacAttributeList = rbacAttributeRepository.findAll().collectList().block();
        assertThat(rbacAttributeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateRbacAttributeWithPatch() throws Exception {
        // Initialize the database
        rbacAttributeRepository.save(rbacAttribute).block();

        int databaseSizeBeforeUpdate = rbacAttributeRepository.findAll().collectList().block().size();

        // Update the rbacAttribute using partial update
        RbacAttribute partialUpdatedRbacAttribute = new RbacAttribute();
        partialUpdatedRbacAttribute.setId(rbacAttribute.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRbacAttribute.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedRbacAttribute))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RbacAttribute in the database
        List<RbacAttribute> rbacAttributeList = rbacAttributeRepository.findAll().collectList().block();
        assertThat(rbacAttributeList).hasSize(databaseSizeBeforeUpdate);
        RbacAttribute testRbacAttribute = rbacAttributeList.get(rbacAttributeList.size() - 1);
        assertThat(testRbacAttribute.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testRbacAttribute.getType()).isEqualTo(DEFAULT_TYPE);
    }

    @Test
    void fullUpdateRbacAttributeWithPatch() throws Exception {
        // Initialize the database
        rbacAttributeRepository.save(rbacAttribute).block();

        int databaseSizeBeforeUpdate = rbacAttributeRepository.findAll().collectList().block().size();

        // Update the rbacAttribute using partial update
        RbacAttribute partialUpdatedRbacAttribute = new RbacAttribute();
        partialUpdatedRbacAttribute.setId(rbacAttribute.getId());

        partialUpdatedRbacAttribute.name(UPDATED_NAME).type(UPDATED_TYPE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRbacAttribute.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedRbacAttribute))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RbacAttribute in the database
        List<RbacAttribute> rbacAttributeList = rbacAttributeRepository.findAll().collectList().block();
        assertThat(rbacAttributeList).hasSize(databaseSizeBeforeUpdate);
        RbacAttribute testRbacAttribute = rbacAttributeList.get(rbacAttributeList.size() - 1);
        assertThat(testRbacAttribute.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testRbacAttribute.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    void patchNonExistingRbacAttribute() throws Exception {
        int databaseSizeBeforeUpdate = rbacAttributeRepository.findAll().collectList().block().size();
        rbacAttribute.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, rbacAttribute.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacAttribute))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RbacAttribute in the database
        List<RbacAttribute> rbacAttributeList = rbacAttributeRepository.findAll().collectList().block();
        assertThat(rbacAttributeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchRbacAttribute() throws Exception {
        int databaseSizeBeforeUpdate = rbacAttributeRepository.findAll().collectList().block().size();
        rbacAttribute.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacAttribute))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RbacAttribute in the database
        List<RbacAttribute> rbacAttributeList = rbacAttributeRepository.findAll().collectList().block();
        assertThat(rbacAttributeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamRbacAttribute() throws Exception {
        int databaseSizeBeforeUpdate = rbacAttributeRepository.findAll().collectList().block().size();
        rbacAttribute.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacAttribute))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the RbacAttribute in the database
        List<RbacAttribute> rbacAttributeList = rbacAttributeRepository.findAll().collectList().block();
        assertThat(rbacAttributeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteRbacAttribute() {
        // Initialize the database
        rbacAttributeRepository.save(rbacAttribute).block();

        int databaseSizeBeforeDelete = rbacAttributeRepository.findAll().collectList().block().size();

        // Delete the rbacAttribute
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, rbacAttribute.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<RbacAttribute> rbacAttributeList = rbacAttributeRepository.findAll().collectList().block();
        assertThat(rbacAttributeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
