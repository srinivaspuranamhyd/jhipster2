package com.dbs.itt.security.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.dbs.itt.security.IntegrationTest;
import com.dbs.itt.security.domain.RbacUser;
import com.dbs.itt.security.domain.enumeration.RbacUserStatus;
import com.dbs.itt.security.repository.RbacUserRepository;
import com.dbs.itt.security.service.EntityManager;
import com.dbs.itt.security.service.RbacUserService;
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
 * Integration tests for the {@link RbacUserResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient
@WithMockUser
class RbacUserResourceIT {

    private static final String DEFAULT_ONEBANK_ID = "AAAAAAAAAA";
    private static final String UPDATED_ONEBANK_ID = "BBBBBBBBBB";

    private static final String DEFAULT_LAN_ID = "AAAAAAAAAA";
    private static final String UPDATED_LAN_ID = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "S&@_w.}l";
    private static final String UPDATED_EMAIL = "&kuRu@po.%X";

    private static final RbacUserStatus DEFAULT_STATUS = RbacUserStatus.ACTIVE;
    private static final RbacUserStatus UPDATED_STATUS = RbacUserStatus.INACTIVE;

    private static final String DEFAULT_DEPARTMENT = "AAAAAAAAAA";
    private static final String UPDATED_DEPARTMENT = "BBBBBBBBBB";

    private static final String DEFAULT_COUNTRY = "AAAAAAAAAA";
    private static final String UPDATED_COUNTRY = "BBBBBBBBBB";

    private static final String DEFAULT_MANAGER_ID = "AAAAAAAAAA";
    private static final String UPDATED_MANAGER_ID = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/rbac-users";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private RbacUserRepository rbacUserRepository;

    @Mock
    private RbacUserRepository rbacUserRepositoryMock;

    @Mock
    private RbacUserService rbacUserServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private RbacUser rbacUser;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RbacUser createEntity(EntityManager em) {
        RbacUser rbacUser = new RbacUser()
            .onebankId(DEFAULT_ONEBANK_ID)
            .lanId(DEFAULT_LAN_ID)
            .email(DEFAULT_EMAIL)
            .status(DEFAULT_STATUS)
            .department(DEFAULT_DEPARTMENT)
            .country(DEFAULT_COUNTRY)
            .managerId(DEFAULT_MANAGER_ID);
        return rbacUser;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RbacUser createUpdatedEntity(EntityManager em) {
        RbacUser rbacUser = new RbacUser()
            .onebankId(UPDATED_ONEBANK_ID)
            .lanId(UPDATED_LAN_ID)
            .email(UPDATED_EMAIL)
            .status(UPDATED_STATUS)
            .department(UPDATED_DEPARTMENT)
            .country(UPDATED_COUNTRY)
            .managerId(UPDATED_MANAGER_ID);
        return rbacUser;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll("rel_rbac_user__group").block();
            em.deleteAll(RbacUser.class).block();
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
        rbacUser = createEntity(em);
    }

    @Test
    void createRbacUser() throws Exception {
        int databaseSizeBeforeCreate = rbacUserRepository.findAll().collectList().block().size();
        // Create the RbacUser
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacUser))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the RbacUser in the database
        List<RbacUser> rbacUserList = rbacUserRepository.findAll().collectList().block();
        assertThat(rbacUserList).hasSize(databaseSizeBeforeCreate + 1);
        RbacUser testRbacUser = rbacUserList.get(rbacUserList.size() - 1);
        assertThat(testRbacUser.getOnebankId()).isEqualTo(DEFAULT_ONEBANK_ID);
        assertThat(testRbacUser.getLanId()).isEqualTo(DEFAULT_LAN_ID);
        assertThat(testRbacUser.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testRbacUser.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testRbacUser.getDepartment()).isEqualTo(DEFAULT_DEPARTMENT);
        assertThat(testRbacUser.getCountry()).isEqualTo(DEFAULT_COUNTRY);
        assertThat(testRbacUser.getManagerId()).isEqualTo(DEFAULT_MANAGER_ID);
    }

    @Test
    void createRbacUserWithExistingId() throws Exception {
        // Create the RbacUser with an existing ID
        rbacUser.setId(1L);

        int databaseSizeBeforeCreate = rbacUserRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacUser))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RbacUser in the database
        List<RbacUser> rbacUserList = rbacUserRepository.findAll().collectList().block();
        assertThat(rbacUserList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkOnebankIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = rbacUserRepository.findAll().collectList().block().size();
        // set the field null
        rbacUser.setOnebankId(null);

        // Create the RbacUser, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacUser))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<RbacUser> rbacUserList = rbacUserRepository.findAll().collectList().block();
        assertThat(rbacUserList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkLanIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = rbacUserRepository.findAll().collectList().block().size();
        // set the field null
        rbacUser.setLanId(null);

        // Create the RbacUser, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacUser))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<RbacUser> rbacUserList = rbacUserRepository.findAll().collectList().block();
        assertThat(rbacUserList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkEmailIsRequired() throws Exception {
        int databaseSizeBeforeTest = rbacUserRepository.findAll().collectList().block().size();
        // set the field null
        rbacUser.setEmail(null);

        // Create the RbacUser, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacUser))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<RbacUser> rbacUserList = rbacUserRepository.findAll().collectList().block();
        assertThat(rbacUserList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = rbacUserRepository.findAll().collectList().block().size();
        // set the field null
        rbacUser.setStatus(null);

        // Create the RbacUser, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacUser))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<RbacUser> rbacUserList = rbacUserRepository.findAll().collectList().block();
        assertThat(rbacUserList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkDepartmentIsRequired() throws Exception {
        int databaseSizeBeforeTest = rbacUserRepository.findAll().collectList().block().size();
        // set the field null
        rbacUser.setDepartment(null);

        // Create the RbacUser, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacUser))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<RbacUser> rbacUserList = rbacUserRepository.findAll().collectList().block();
        assertThat(rbacUserList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkCountryIsRequired() throws Exception {
        int databaseSizeBeforeTest = rbacUserRepository.findAll().collectList().block().size();
        // set the field null
        rbacUser.setCountry(null);

        // Create the RbacUser, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacUser))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<RbacUser> rbacUserList = rbacUserRepository.findAll().collectList().block();
        assertThat(rbacUserList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllRbacUsers() {
        // Initialize the database
        rbacUserRepository.save(rbacUser).block();

        // Get all the rbacUserList
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
            .value(hasItem(rbacUser.getId().intValue()))
            .jsonPath("$.[*].onebankId")
            .value(hasItem(DEFAULT_ONEBANK_ID))
            .jsonPath("$.[*].lanId")
            .value(hasItem(DEFAULT_LAN_ID))
            .jsonPath("$.[*].email")
            .value(hasItem(DEFAULT_EMAIL))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()))
            .jsonPath("$.[*].department")
            .value(hasItem(DEFAULT_DEPARTMENT))
            .jsonPath("$.[*].country")
            .value(hasItem(DEFAULT_COUNTRY))
            .jsonPath("$.[*].managerId")
            .value(hasItem(DEFAULT_MANAGER_ID));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllRbacUsersWithEagerRelationshipsIsEnabled() {
        when(rbacUserServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(rbacUserServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllRbacUsersWithEagerRelationshipsIsNotEnabled() {
        when(rbacUserServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(rbacUserServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getRbacUser() {
        // Initialize the database
        rbacUserRepository.save(rbacUser).block();

        // Get the rbacUser
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, rbacUser.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(rbacUser.getId().intValue()))
            .jsonPath("$.onebankId")
            .value(is(DEFAULT_ONEBANK_ID))
            .jsonPath("$.lanId")
            .value(is(DEFAULT_LAN_ID))
            .jsonPath("$.email")
            .value(is(DEFAULT_EMAIL))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()))
            .jsonPath("$.department")
            .value(is(DEFAULT_DEPARTMENT))
            .jsonPath("$.country")
            .value(is(DEFAULT_COUNTRY))
            .jsonPath("$.managerId")
            .value(is(DEFAULT_MANAGER_ID));
    }

    @Test
    void getNonExistingRbacUser() {
        // Get the rbacUser
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewRbacUser() throws Exception {
        // Initialize the database
        rbacUserRepository.save(rbacUser).block();

        int databaseSizeBeforeUpdate = rbacUserRepository.findAll().collectList().block().size();

        // Update the rbacUser
        RbacUser updatedRbacUser = rbacUserRepository.findById(rbacUser.getId()).block();
        updatedRbacUser
            .onebankId(UPDATED_ONEBANK_ID)
            .lanId(UPDATED_LAN_ID)
            .email(UPDATED_EMAIL)
            .status(UPDATED_STATUS)
            .department(UPDATED_DEPARTMENT)
            .country(UPDATED_COUNTRY)
            .managerId(UPDATED_MANAGER_ID);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedRbacUser.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedRbacUser))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RbacUser in the database
        List<RbacUser> rbacUserList = rbacUserRepository.findAll().collectList().block();
        assertThat(rbacUserList).hasSize(databaseSizeBeforeUpdate);
        RbacUser testRbacUser = rbacUserList.get(rbacUserList.size() - 1);
        assertThat(testRbacUser.getOnebankId()).isEqualTo(UPDATED_ONEBANK_ID);
        assertThat(testRbacUser.getLanId()).isEqualTo(UPDATED_LAN_ID);
        assertThat(testRbacUser.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testRbacUser.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testRbacUser.getDepartment()).isEqualTo(UPDATED_DEPARTMENT);
        assertThat(testRbacUser.getCountry()).isEqualTo(UPDATED_COUNTRY);
        assertThat(testRbacUser.getManagerId()).isEqualTo(UPDATED_MANAGER_ID);
    }

    @Test
    void putNonExistingRbacUser() throws Exception {
        int databaseSizeBeforeUpdate = rbacUserRepository.findAll().collectList().block().size();
        rbacUser.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, rbacUser.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacUser))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RbacUser in the database
        List<RbacUser> rbacUserList = rbacUserRepository.findAll().collectList().block();
        assertThat(rbacUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchRbacUser() throws Exception {
        int databaseSizeBeforeUpdate = rbacUserRepository.findAll().collectList().block().size();
        rbacUser.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacUser))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RbacUser in the database
        List<RbacUser> rbacUserList = rbacUserRepository.findAll().collectList().block();
        assertThat(rbacUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamRbacUser() throws Exception {
        int databaseSizeBeforeUpdate = rbacUserRepository.findAll().collectList().block().size();
        rbacUser.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacUser))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the RbacUser in the database
        List<RbacUser> rbacUserList = rbacUserRepository.findAll().collectList().block();
        assertThat(rbacUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateRbacUserWithPatch() throws Exception {
        // Initialize the database
        rbacUserRepository.save(rbacUser).block();

        int databaseSizeBeforeUpdate = rbacUserRepository.findAll().collectList().block().size();

        // Update the rbacUser using partial update
        RbacUser partialUpdatedRbacUser = new RbacUser();
        partialUpdatedRbacUser.setId(rbacUser.getId());

        partialUpdatedRbacUser.onebankId(UPDATED_ONEBANK_ID).lanId(UPDATED_LAN_ID).email(UPDATED_EMAIL).managerId(UPDATED_MANAGER_ID);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRbacUser.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedRbacUser))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RbacUser in the database
        List<RbacUser> rbacUserList = rbacUserRepository.findAll().collectList().block();
        assertThat(rbacUserList).hasSize(databaseSizeBeforeUpdate);
        RbacUser testRbacUser = rbacUserList.get(rbacUserList.size() - 1);
        assertThat(testRbacUser.getOnebankId()).isEqualTo(UPDATED_ONEBANK_ID);
        assertThat(testRbacUser.getLanId()).isEqualTo(UPDATED_LAN_ID);
        assertThat(testRbacUser.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testRbacUser.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testRbacUser.getDepartment()).isEqualTo(DEFAULT_DEPARTMENT);
        assertThat(testRbacUser.getCountry()).isEqualTo(DEFAULT_COUNTRY);
        assertThat(testRbacUser.getManagerId()).isEqualTo(UPDATED_MANAGER_ID);
    }

    @Test
    void fullUpdateRbacUserWithPatch() throws Exception {
        // Initialize the database
        rbacUserRepository.save(rbacUser).block();

        int databaseSizeBeforeUpdate = rbacUserRepository.findAll().collectList().block().size();

        // Update the rbacUser using partial update
        RbacUser partialUpdatedRbacUser = new RbacUser();
        partialUpdatedRbacUser.setId(rbacUser.getId());

        partialUpdatedRbacUser
            .onebankId(UPDATED_ONEBANK_ID)
            .lanId(UPDATED_LAN_ID)
            .email(UPDATED_EMAIL)
            .status(UPDATED_STATUS)
            .department(UPDATED_DEPARTMENT)
            .country(UPDATED_COUNTRY)
            .managerId(UPDATED_MANAGER_ID);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRbacUser.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedRbacUser))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RbacUser in the database
        List<RbacUser> rbacUserList = rbacUserRepository.findAll().collectList().block();
        assertThat(rbacUserList).hasSize(databaseSizeBeforeUpdate);
        RbacUser testRbacUser = rbacUserList.get(rbacUserList.size() - 1);
        assertThat(testRbacUser.getOnebankId()).isEqualTo(UPDATED_ONEBANK_ID);
        assertThat(testRbacUser.getLanId()).isEqualTo(UPDATED_LAN_ID);
        assertThat(testRbacUser.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testRbacUser.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testRbacUser.getDepartment()).isEqualTo(UPDATED_DEPARTMENT);
        assertThat(testRbacUser.getCountry()).isEqualTo(UPDATED_COUNTRY);
        assertThat(testRbacUser.getManagerId()).isEqualTo(UPDATED_MANAGER_ID);
    }

    @Test
    void patchNonExistingRbacUser() throws Exception {
        int databaseSizeBeforeUpdate = rbacUserRepository.findAll().collectList().block().size();
        rbacUser.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, rbacUser.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacUser))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RbacUser in the database
        List<RbacUser> rbacUserList = rbacUserRepository.findAll().collectList().block();
        assertThat(rbacUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchRbacUser() throws Exception {
        int databaseSizeBeforeUpdate = rbacUserRepository.findAll().collectList().block().size();
        rbacUser.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacUser))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RbacUser in the database
        List<RbacUser> rbacUserList = rbacUserRepository.findAll().collectList().block();
        assertThat(rbacUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamRbacUser() throws Exception {
        int databaseSizeBeforeUpdate = rbacUserRepository.findAll().collectList().block().size();
        rbacUser.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(rbacUser))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the RbacUser in the database
        List<RbacUser> rbacUserList = rbacUserRepository.findAll().collectList().block();
        assertThat(rbacUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteRbacUser() {
        // Initialize the database
        rbacUserRepository.save(rbacUser).block();

        int databaseSizeBeforeDelete = rbacUserRepository.findAll().collectList().block().size();

        // Delete the rbacUser
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, rbacUser.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<RbacUser> rbacUserList = rbacUserRepository.findAll().collectList().block();
        assertThat(rbacUserList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
