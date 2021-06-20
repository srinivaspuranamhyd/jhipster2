package com.dbs.itt.security.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.dbs.itt.security.IntegrationTest;
import com.dbs.itt.security.domain.RbacSecurityGroup;
import com.dbs.itt.security.repository.RbacSecurityGroupRepository;
import com.dbs.itt.security.service.EntityManager;
import com.dbs.itt.security.service.RbacSecurityGroupService;
import java.time.Duration;
import java.util.ArrayList;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Integration tests for the {@link RbacSecurityGroupResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient
@WithMockUser
class RbacSecurityGroupResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESC = "AAAAAAAAAA";
    private static final String UPDATED_DESC = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/rbac-security-groups";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private RbacSecurityGroupRepository rbacSecurityGroupRepository;

    @Mock
    private RbacSecurityGroupRepository rbacSecurityGroupRepositoryMock;

    @Mock
    private RbacSecurityGroupService rbacSecurityGroupServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private RbacSecurityGroup rbacSecurityGroup;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RbacSecurityGroup createEntity(EntityManager em) {
        RbacSecurityGroup rbacSecurityGroup = new RbacSecurityGroup().name(DEFAULT_NAME).desc(DEFAULT_DESC);
        return rbacSecurityGroup;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RbacSecurityGroup createUpdatedEntity(EntityManager em) {
        RbacSecurityGroup rbacSecurityGroup = new RbacSecurityGroup().name(UPDATED_NAME).desc(UPDATED_DESC);
        return rbacSecurityGroup;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll("rel_rbac_security_group__data_topic").block();
            em.deleteAll(RbacSecurityGroup.class).block();
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
        rbacSecurityGroup = createEntity(em);
    }

    @Test
    void createRbacSecurityGroup() throws Exception {
        int databaseSizeBeforeCreate = rbacSecurityGroupRepository.findAll().collectList().block().size();
        // Create the RbacSecurityGroup
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacSecurityGroup))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the RbacSecurityGroup in the database
        List<RbacSecurityGroup> rbacSecurityGroupList = rbacSecurityGroupRepository.findAll().collectList().block();
        assertThat(rbacSecurityGroupList).hasSize(databaseSizeBeforeCreate + 1);
        RbacSecurityGroup testRbacSecurityGroup = rbacSecurityGroupList.get(rbacSecurityGroupList.size() - 1);
        assertThat(testRbacSecurityGroup.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testRbacSecurityGroup.getDesc()).isEqualTo(DEFAULT_DESC);
    }

    @Test
    void createRbacSecurityGroupWithExistingId() throws Exception {
        // Create the RbacSecurityGroup with an existing ID
        rbacSecurityGroup.setId(1L);

        int databaseSizeBeforeCreate = rbacSecurityGroupRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacSecurityGroup))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RbacSecurityGroup in the database
        List<RbacSecurityGroup> rbacSecurityGroupList = rbacSecurityGroupRepository.findAll().collectList().block();
        assertThat(rbacSecurityGroupList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = rbacSecurityGroupRepository.findAll().collectList().block().size();
        // set the field null
        rbacSecurityGroup.setName(null);

        // Create the RbacSecurityGroup, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacSecurityGroup))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<RbacSecurityGroup> rbacSecurityGroupList = rbacSecurityGroupRepository.findAll().collectList().block();
        assertThat(rbacSecurityGroupList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkDescIsRequired() throws Exception {
        int databaseSizeBeforeTest = rbacSecurityGroupRepository.findAll().collectList().block().size();
        // set the field null
        rbacSecurityGroup.setDesc(null);

        // Create the RbacSecurityGroup, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacSecurityGroup))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<RbacSecurityGroup> rbacSecurityGroupList = rbacSecurityGroupRepository.findAll().collectList().block();
        assertThat(rbacSecurityGroupList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllRbacSecurityGroups() {
        // Initialize the database
        rbacSecurityGroupRepository.save(rbacSecurityGroup).block();

        // Get all the rbacSecurityGroupList
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
            .value(hasItem(rbacSecurityGroup.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].desc")
            .value(hasItem(DEFAULT_DESC));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllRbacSecurityGroupsWithEagerRelationshipsIsEnabled() {
        when(rbacSecurityGroupServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(rbacSecurityGroupServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllRbacSecurityGroupsWithEagerRelationshipsIsNotEnabled() {
        when(rbacSecurityGroupServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(rbacSecurityGroupServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getRbacSecurityGroup() {
        // Initialize the database
        rbacSecurityGroupRepository.save(rbacSecurityGroup).block();

        // Get the rbacSecurityGroup
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, rbacSecurityGroup.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(rbacSecurityGroup.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.desc")
            .value(is(DEFAULT_DESC));
    }

    @Test
    void getNonExistingRbacSecurityGroup() {
        // Get the rbacSecurityGroup
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewRbacSecurityGroup() throws Exception {
        // Initialize the database
        rbacSecurityGroupRepository.save(rbacSecurityGroup).block();

        int databaseSizeBeforeUpdate = rbacSecurityGroupRepository.findAll().collectList().block().size();

        // Update the rbacSecurityGroup
        RbacSecurityGroup updatedRbacSecurityGroup = rbacSecurityGroupRepository.findById(rbacSecurityGroup.getId()).block();
        updatedRbacSecurityGroup.name(UPDATED_NAME).desc(UPDATED_DESC);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedRbacSecurityGroup.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedRbacSecurityGroup))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RbacSecurityGroup in the database
        List<RbacSecurityGroup> rbacSecurityGroupList = rbacSecurityGroupRepository.findAll().collectList().block();
        assertThat(rbacSecurityGroupList).hasSize(databaseSizeBeforeUpdate);
        RbacSecurityGroup testRbacSecurityGroup = rbacSecurityGroupList.get(rbacSecurityGroupList.size() - 1);
        assertThat(testRbacSecurityGroup.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testRbacSecurityGroup.getDesc()).isEqualTo(UPDATED_DESC);
    }

    @Test
    void putNonExistingRbacSecurityGroup() throws Exception {
        int databaseSizeBeforeUpdate = rbacSecurityGroupRepository.findAll().collectList().block().size();
        rbacSecurityGroup.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, rbacSecurityGroup.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacSecurityGroup))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RbacSecurityGroup in the database
        List<RbacSecurityGroup> rbacSecurityGroupList = rbacSecurityGroupRepository.findAll().collectList().block();
        assertThat(rbacSecurityGroupList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchRbacSecurityGroup() throws Exception {
        int databaseSizeBeforeUpdate = rbacSecurityGroupRepository.findAll().collectList().block().size();
        rbacSecurityGroup.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacSecurityGroup))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RbacSecurityGroup in the database
        List<RbacSecurityGroup> rbacSecurityGroupList = rbacSecurityGroupRepository.findAll().collectList().block();
        assertThat(rbacSecurityGroupList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamRbacSecurityGroup() throws Exception {
        int databaseSizeBeforeUpdate = rbacSecurityGroupRepository.findAll().collectList().block().size();
        rbacSecurityGroup.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacSecurityGroup))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the RbacSecurityGroup in the database
        List<RbacSecurityGroup> rbacSecurityGroupList = rbacSecurityGroupRepository.findAll().collectList().block();
        assertThat(rbacSecurityGroupList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateRbacSecurityGroupWithPatch() throws Exception {
        // Initialize the database
        rbacSecurityGroupRepository.save(rbacSecurityGroup).block();

        int databaseSizeBeforeUpdate = rbacSecurityGroupRepository.findAll().collectList().block().size();

        // Update the rbacSecurityGroup using partial update
        RbacSecurityGroup partialUpdatedRbacSecurityGroup = new RbacSecurityGroup();
        partialUpdatedRbacSecurityGroup.setId(rbacSecurityGroup.getId());

        partialUpdatedRbacSecurityGroup.name(UPDATED_NAME).desc(UPDATED_DESC);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRbacSecurityGroup.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedRbacSecurityGroup))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RbacSecurityGroup in the database
        List<RbacSecurityGroup> rbacSecurityGroupList = rbacSecurityGroupRepository.findAll().collectList().block();
        assertThat(rbacSecurityGroupList).hasSize(databaseSizeBeforeUpdate);
        RbacSecurityGroup testRbacSecurityGroup = rbacSecurityGroupList.get(rbacSecurityGroupList.size() - 1);
        assertThat(testRbacSecurityGroup.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testRbacSecurityGroup.getDesc()).isEqualTo(UPDATED_DESC);
    }

    @Test
    void fullUpdateRbacSecurityGroupWithPatch() throws Exception {
        // Initialize the database
        rbacSecurityGroupRepository.save(rbacSecurityGroup).block();

        int databaseSizeBeforeUpdate = rbacSecurityGroupRepository.findAll().collectList().block().size();

        // Update the rbacSecurityGroup using partial update
        RbacSecurityGroup partialUpdatedRbacSecurityGroup = new RbacSecurityGroup();
        partialUpdatedRbacSecurityGroup.setId(rbacSecurityGroup.getId());

        partialUpdatedRbacSecurityGroup.name(UPDATED_NAME).desc(UPDATED_DESC);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRbacSecurityGroup.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedRbacSecurityGroup))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RbacSecurityGroup in the database
        List<RbacSecurityGroup> rbacSecurityGroupList = rbacSecurityGroupRepository.findAll().collectList().block();
        assertThat(rbacSecurityGroupList).hasSize(databaseSizeBeforeUpdate);
        RbacSecurityGroup testRbacSecurityGroup = rbacSecurityGroupList.get(rbacSecurityGroupList.size() - 1);
        assertThat(testRbacSecurityGroup.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testRbacSecurityGroup.getDesc()).isEqualTo(UPDATED_DESC);
    }

    @Test
    void patchNonExistingRbacSecurityGroup() throws Exception {
        int databaseSizeBeforeUpdate = rbacSecurityGroupRepository.findAll().collectList().block().size();
        rbacSecurityGroup.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, rbacSecurityGroup.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacSecurityGroup))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RbacSecurityGroup in the database
        List<RbacSecurityGroup> rbacSecurityGroupList = rbacSecurityGroupRepository.findAll().collectList().block();
        assertThat(rbacSecurityGroupList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchRbacSecurityGroup() throws Exception {
        int databaseSizeBeforeUpdate = rbacSecurityGroupRepository.findAll().collectList().block().size();
        rbacSecurityGroup.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacSecurityGroup))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RbacSecurityGroup in the database
        List<RbacSecurityGroup> rbacSecurityGroupList = rbacSecurityGroupRepository.findAll().collectList().block();
        assertThat(rbacSecurityGroupList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamRbacSecurityGroup() throws Exception {
        int databaseSizeBeforeUpdate = rbacSecurityGroupRepository.findAll().collectList().block().size();
        rbacSecurityGroup.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacSecurityGroup))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the RbacSecurityGroup in the database
        List<RbacSecurityGroup> rbacSecurityGroupList = rbacSecurityGroupRepository.findAll().collectList().block();
        assertThat(rbacSecurityGroupList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteRbacSecurityGroup() {
        // Initialize the database
        rbacSecurityGroupRepository.save(rbacSecurityGroup).block();

        int databaseSizeBeforeDelete = rbacSecurityGroupRepository.findAll().collectList().block().size();

        // Delete the rbacSecurityGroup
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, rbacSecurityGroup.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<RbacSecurityGroup> rbacSecurityGroupList = rbacSecurityGroupRepository.findAll().collectList().block();
        assertThat(rbacSecurityGroupList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
