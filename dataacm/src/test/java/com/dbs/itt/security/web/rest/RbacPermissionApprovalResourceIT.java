package com.dbs.itt.security.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.dbs.itt.security.IntegrationTest;
import com.dbs.itt.security.domain.RbacPermissionApproval;
import com.dbs.itt.security.domain.enumeration.RbacPermissionApprovalStatus;
import com.dbs.itt.security.repository.RbacPermissionApprovalRepository;
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
 * Integration tests for the {@link RbacPermissionApprovalResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class RbacPermissionApprovalResourceIT {

    private static final String DEFAULT_DESC = "AAAAAAAAAA";
    private static final String UPDATED_DESC = "BBBBBBBBBB";

    private static final String DEFAULT_APPROVER_EMAIL = "1\\d5~@VcS_0.\\e',7Q";
    private static final String UPDATED_APPROVER_EMAIL = "[t!k@A\\T=AS.5";

    private static final RbacPermissionApprovalStatus DEFAULT_STATUS = RbacPermissionApprovalStatus.APPROVED;
    private static final RbacPermissionApprovalStatus UPDATED_STATUS = RbacPermissionApprovalStatus.PENDING;

    private static final String ENTITY_API_URL = "/api/rbac-permission-approvals";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private RbacPermissionApprovalRepository rbacPermissionApprovalRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private RbacPermissionApproval rbacPermissionApproval;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RbacPermissionApproval createEntity(EntityManager em) {
        RbacPermissionApproval rbacPermissionApproval = new RbacPermissionApproval()
            .desc(DEFAULT_DESC)
            .approverEmail(DEFAULT_APPROVER_EMAIL)
            .status(DEFAULT_STATUS);
        return rbacPermissionApproval;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RbacPermissionApproval createUpdatedEntity(EntityManager em) {
        RbacPermissionApproval rbacPermissionApproval = new RbacPermissionApproval()
            .desc(UPDATED_DESC)
            .approverEmail(UPDATED_APPROVER_EMAIL)
            .status(UPDATED_STATUS);
        return rbacPermissionApproval;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(RbacPermissionApproval.class).block();
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
        rbacPermissionApproval = createEntity(em);
    }

    @Test
    void createRbacPermissionApproval() throws Exception {
        int databaseSizeBeforeCreate = rbacPermissionApprovalRepository.findAll().collectList().block().size();
        // Create the RbacPermissionApproval
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacPermissionApproval))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the RbacPermissionApproval in the database
        List<RbacPermissionApproval> rbacPermissionApprovalList = rbacPermissionApprovalRepository.findAll().collectList().block();
        assertThat(rbacPermissionApprovalList).hasSize(databaseSizeBeforeCreate + 1);
        RbacPermissionApproval testRbacPermissionApproval = rbacPermissionApprovalList.get(rbacPermissionApprovalList.size() - 1);
        assertThat(testRbacPermissionApproval.getDesc()).isEqualTo(DEFAULT_DESC);
        assertThat(testRbacPermissionApproval.getApproverEmail()).isEqualTo(DEFAULT_APPROVER_EMAIL);
        assertThat(testRbacPermissionApproval.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    void createRbacPermissionApprovalWithExistingId() throws Exception {
        // Create the RbacPermissionApproval with an existing ID
        rbacPermissionApproval.setId(1L);

        int databaseSizeBeforeCreate = rbacPermissionApprovalRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacPermissionApproval))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RbacPermissionApproval in the database
        List<RbacPermissionApproval> rbacPermissionApprovalList = rbacPermissionApprovalRepository.findAll().collectList().block();
        assertThat(rbacPermissionApprovalList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkDescIsRequired() throws Exception {
        int databaseSizeBeforeTest = rbacPermissionApprovalRepository.findAll().collectList().block().size();
        // set the field null
        rbacPermissionApproval.setDesc(null);

        // Create the RbacPermissionApproval, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacPermissionApproval))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<RbacPermissionApproval> rbacPermissionApprovalList = rbacPermissionApprovalRepository.findAll().collectList().block();
        assertThat(rbacPermissionApprovalList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkApproverEmailIsRequired() throws Exception {
        int databaseSizeBeforeTest = rbacPermissionApprovalRepository.findAll().collectList().block().size();
        // set the field null
        rbacPermissionApproval.setApproverEmail(null);

        // Create the RbacPermissionApproval, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacPermissionApproval))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<RbacPermissionApproval> rbacPermissionApprovalList = rbacPermissionApprovalRepository.findAll().collectList().block();
        assertThat(rbacPermissionApprovalList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = rbacPermissionApprovalRepository.findAll().collectList().block().size();
        // set the field null
        rbacPermissionApproval.setStatus(null);

        // Create the RbacPermissionApproval, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacPermissionApproval))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<RbacPermissionApproval> rbacPermissionApprovalList = rbacPermissionApprovalRepository.findAll().collectList().block();
        assertThat(rbacPermissionApprovalList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllRbacPermissionApprovals() {
        // Initialize the database
        rbacPermissionApprovalRepository.save(rbacPermissionApproval).block();

        // Get all the rbacPermissionApprovalList
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
            .value(hasItem(rbacPermissionApproval.getId().intValue()))
            .jsonPath("$.[*].desc")
            .value(hasItem(DEFAULT_DESC))
            .jsonPath("$.[*].approverEmail")
            .value(hasItem(DEFAULT_APPROVER_EMAIL))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()));
    }

    @Test
    void getRbacPermissionApproval() {
        // Initialize the database
        rbacPermissionApprovalRepository.save(rbacPermissionApproval).block();

        // Get the rbacPermissionApproval
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, rbacPermissionApproval.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(rbacPermissionApproval.getId().intValue()))
            .jsonPath("$.desc")
            .value(is(DEFAULT_DESC))
            .jsonPath("$.approverEmail")
            .value(is(DEFAULT_APPROVER_EMAIL))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()));
    }

    @Test
    void getNonExistingRbacPermissionApproval() {
        // Get the rbacPermissionApproval
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewRbacPermissionApproval() throws Exception {
        // Initialize the database
        rbacPermissionApprovalRepository.save(rbacPermissionApproval).block();

        int databaseSizeBeforeUpdate = rbacPermissionApprovalRepository.findAll().collectList().block().size();

        // Update the rbacPermissionApproval
        RbacPermissionApproval updatedRbacPermissionApproval = rbacPermissionApprovalRepository
            .findById(rbacPermissionApproval.getId())
            .block();
        updatedRbacPermissionApproval.desc(UPDATED_DESC).approverEmail(UPDATED_APPROVER_EMAIL).status(UPDATED_STATUS);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedRbacPermissionApproval.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedRbacPermissionApproval))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RbacPermissionApproval in the database
        List<RbacPermissionApproval> rbacPermissionApprovalList = rbacPermissionApprovalRepository.findAll().collectList().block();
        assertThat(rbacPermissionApprovalList).hasSize(databaseSizeBeforeUpdate);
        RbacPermissionApproval testRbacPermissionApproval = rbacPermissionApprovalList.get(rbacPermissionApprovalList.size() - 1);
        assertThat(testRbacPermissionApproval.getDesc()).isEqualTo(UPDATED_DESC);
        assertThat(testRbacPermissionApproval.getApproverEmail()).isEqualTo(UPDATED_APPROVER_EMAIL);
        assertThat(testRbacPermissionApproval.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    void putNonExistingRbacPermissionApproval() throws Exception {
        int databaseSizeBeforeUpdate = rbacPermissionApprovalRepository.findAll().collectList().block().size();
        rbacPermissionApproval.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, rbacPermissionApproval.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacPermissionApproval))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RbacPermissionApproval in the database
        List<RbacPermissionApproval> rbacPermissionApprovalList = rbacPermissionApprovalRepository.findAll().collectList().block();
        assertThat(rbacPermissionApprovalList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchRbacPermissionApproval() throws Exception {
        int databaseSizeBeforeUpdate = rbacPermissionApprovalRepository.findAll().collectList().block().size();
        rbacPermissionApproval.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacPermissionApproval))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RbacPermissionApproval in the database
        List<RbacPermissionApproval> rbacPermissionApprovalList = rbacPermissionApprovalRepository.findAll().collectList().block();
        assertThat(rbacPermissionApprovalList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamRbacPermissionApproval() throws Exception {
        int databaseSizeBeforeUpdate = rbacPermissionApprovalRepository.findAll().collectList().block().size();
        rbacPermissionApproval.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacPermissionApproval))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the RbacPermissionApproval in the database
        List<RbacPermissionApproval> rbacPermissionApprovalList = rbacPermissionApprovalRepository.findAll().collectList().block();
        assertThat(rbacPermissionApprovalList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateRbacPermissionApprovalWithPatch() throws Exception {
        // Initialize the database
        rbacPermissionApprovalRepository.save(rbacPermissionApproval).block();

        int databaseSizeBeforeUpdate = rbacPermissionApprovalRepository.findAll().collectList().block().size();

        // Update the rbacPermissionApproval using partial update
        RbacPermissionApproval partialUpdatedRbacPermissionApproval = new RbacPermissionApproval();
        partialUpdatedRbacPermissionApproval.setId(rbacPermissionApproval.getId());

        partialUpdatedRbacPermissionApproval.approverEmail(UPDATED_APPROVER_EMAIL).status(UPDATED_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRbacPermissionApproval.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedRbacPermissionApproval))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RbacPermissionApproval in the database
        List<RbacPermissionApproval> rbacPermissionApprovalList = rbacPermissionApprovalRepository.findAll().collectList().block();
        assertThat(rbacPermissionApprovalList).hasSize(databaseSizeBeforeUpdate);
        RbacPermissionApproval testRbacPermissionApproval = rbacPermissionApprovalList.get(rbacPermissionApprovalList.size() - 1);
        assertThat(testRbacPermissionApproval.getDesc()).isEqualTo(DEFAULT_DESC);
        assertThat(testRbacPermissionApproval.getApproverEmail()).isEqualTo(UPDATED_APPROVER_EMAIL);
        assertThat(testRbacPermissionApproval.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    void fullUpdateRbacPermissionApprovalWithPatch() throws Exception {
        // Initialize the database
        rbacPermissionApprovalRepository.save(rbacPermissionApproval).block();

        int databaseSizeBeforeUpdate = rbacPermissionApprovalRepository.findAll().collectList().block().size();

        // Update the rbacPermissionApproval using partial update
        RbacPermissionApproval partialUpdatedRbacPermissionApproval = new RbacPermissionApproval();
        partialUpdatedRbacPermissionApproval.setId(rbacPermissionApproval.getId());

        partialUpdatedRbacPermissionApproval.desc(UPDATED_DESC).approverEmail(UPDATED_APPROVER_EMAIL).status(UPDATED_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRbacPermissionApproval.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedRbacPermissionApproval))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RbacPermissionApproval in the database
        List<RbacPermissionApproval> rbacPermissionApprovalList = rbacPermissionApprovalRepository.findAll().collectList().block();
        assertThat(rbacPermissionApprovalList).hasSize(databaseSizeBeforeUpdate);
        RbacPermissionApproval testRbacPermissionApproval = rbacPermissionApprovalList.get(rbacPermissionApprovalList.size() - 1);
        assertThat(testRbacPermissionApproval.getDesc()).isEqualTo(UPDATED_DESC);
        assertThat(testRbacPermissionApproval.getApproverEmail()).isEqualTo(UPDATED_APPROVER_EMAIL);
        assertThat(testRbacPermissionApproval.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    void patchNonExistingRbacPermissionApproval() throws Exception {
        int databaseSizeBeforeUpdate = rbacPermissionApprovalRepository.findAll().collectList().block().size();
        rbacPermissionApproval.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, rbacPermissionApproval.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacPermissionApproval))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RbacPermissionApproval in the database
        List<RbacPermissionApproval> rbacPermissionApprovalList = rbacPermissionApprovalRepository.findAll().collectList().block();
        assertThat(rbacPermissionApprovalList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchRbacPermissionApproval() throws Exception {
        int databaseSizeBeforeUpdate = rbacPermissionApprovalRepository.findAll().collectList().block().size();
        rbacPermissionApproval.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacPermissionApproval))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RbacPermissionApproval in the database
        List<RbacPermissionApproval> rbacPermissionApprovalList = rbacPermissionApprovalRepository.findAll().collectList().block();
        assertThat(rbacPermissionApprovalList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamRbacPermissionApproval() throws Exception {
        int databaseSizeBeforeUpdate = rbacPermissionApprovalRepository.findAll().collectList().block().size();
        rbacPermissionApproval.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacPermissionApproval))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the RbacPermissionApproval in the database
        List<RbacPermissionApproval> rbacPermissionApprovalList = rbacPermissionApprovalRepository.findAll().collectList().block();
        assertThat(rbacPermissionApprovalList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteRbacPermissionApproval() {
        // Initialize the database
        rbacPermissionApprovalRepository.save(rbacPermissionApproval).block();

        int databaseSizeBeforeDelete = rbacPermissionApprovalRepository.findAll().collectList().block().size();

        // Delete the rbacPermissionApproval
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, rbacPermissionApproval.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<RbacPermissionApproval> rbacPermissionApprovalList = rbacPermissionApprovalRepository.findAll().collectList().block();
        assertThat(rbacPermissionApprovalList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
