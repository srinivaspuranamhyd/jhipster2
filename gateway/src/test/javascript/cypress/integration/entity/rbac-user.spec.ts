import {
  entityTableSelector,
  entityDetailsButtonSelector,
  entityDetailsBackButtonSelector,
  entityCreateButtonSelector,
  entityCreateSaveButtonSelector,
  entityEditButtonSelector,
  entityDeleteButtonSelector,
  entityConfirmDeleteButtonSelector,
} from '../../support/entity';

describe('RbacUser e2e test', () => {
  let startingEntitiesCount = 0;

  beforeEach(() => {
    cy.getOauth2Data();
    cy.get('@oauth2Data').then(oauth2Data => {
      cy.oauthLogin(oauth2Data, Cypress.env('E2E_USERNAME') || 'admin', Cypress.env('E2E_PASSWORD') || 'admin');
    });
    cy.intercept('GET', '/services/dataacm/api/rbac-users*').as('entitiesRequest');
    cy.visit('');
    cy.clickOnEntityMenuItem('rbac-user');
    cy.wait('@entitiesRequest').then(({ request, response }) => (startingEntitiesCount = response.body.length));
    cy.visit('/');
  });

  afterEach(() => {
    cy.get('@oauth2Data').then(oauth2Data => {
      cy.oauthLogout(oauth2Data);
    });
    cy.clearCache();
  });

  it('should load RbacUsers', () => {
    cy.intercept('GET', '/services/dataacm/api/rbac-users*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('rbac-user');
    cy.wait('@entitiesRequest');
    cy.getEntityHeading('RbacUser').should('exist');
    if (startingEntitiesCount === 0) {
      cy.get(entityTableSelector).should('not.exist');
    } else {
      cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount);
    }
    cy.visit('/');
  });

  it('should load details RbacUser page', () => {
    cy.intercept('GET', '/services/dataacm/api/rbac-users*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('rbac-user');
    cy.wait('@entitiesRequest');
    if (startingEntitiesCount > 0) {
      cy.get(entityDetailsButtonSelector).first().click({ force: true });
      cy.getEntityDetailsHeading('rbacUser');
      cy.get(entityDetailsBackButtonSelector).should('exist');
    }
    cy.visit('/');
  });

  it('should load create RbacUser page', () => {
    cy.intercept('GET', '/services/dataacm/api/rbac-users*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('rbac-user');
    cy.wait('@entitiesRequest');
    cy.get(entityCreateButtonSelector).click({ force: true });
    cy.getEntityCreateUpdateHeading('RbacUser');
    cy.get(entityCreateSaveButtonSelector).should('exist');
    cy.visit('/');
  });

  it('should load edit RbacUser page', () => {
    cy.intercept('GET', '/services/dataacm/api/rbac-users*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('rbac-user');
    cy.wait('@entitiesRequest');
    if (startingEntitiesCount > 0) {
      cy.get(entityEditButtonSelector).first().click({ force: true });
      cy.getEntityCreateUpdateHeading('RbacUser');
      cy.get(entityCreateSaveButtonSelector).should('exist');
    }
    cy.visit('/');
  });

  it('should create an instance of RbacUser', () => {
    cy.intercept('GET', '/services/dataacm/api/rbac-users*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('rbac-user');
    cy.wait('@entitiesRequest').then(({ request, response }) => (startingEntitiesCount = response.body.length));
    cy.get(entityCreateButtonSelector).click({ force: true });
    cy.getEntityCreateUpdateHeading('RbacUser');

    cy.get(`[data-cy="onebankId"]`)
      .type('Refined Avon Handcrafted', { force: true })
      .invoke('val')
      .should('match', new RegExp('Refined Avon Handcrafted'));

    cy.get(`[data-cy="lanId"]`).type('Somalia framework', { force: true }).invoke('val').should('match', new RegExp('Somalia framework'));

    cy.get(`[data-cy="email"]`).type('*$1TA!@L.(q', { force: true }).invoke('val').should('match', new RegExp('*$1TA!@L.(q'));

    cy.get(`[data-cy="status"]`).select('INACTIVE');

    cy.get(`[data-cy="department"]`).type('purple', { force: true }).invoke('val').should('match', new RegExp('purple'));

    cy.get(`[data-cy="country"]`).type('Latvia', { force: true }).invoke('val').should('match', new RegExp('Latvia'));

    cy.get(`[data-cy="managerId"]`).type('matrix', { force: true }).invoke('val').should('match', new RegExp('matrix'));

    cy.setFieldSelectToLastOfEntity('group');

    cy.get(entityCreateSaveButtonSelector).click({ force: true });
    cy.scrollTo('top', { ensureScrollable: false });
    cy.get(entityCreateSaveButtonSelector).should('not.exist');
    cy.intercept('GET', '/services/dataacm/api/rbac-users*').as('entitiesRequestAfterCreate');
    cy.visit('/');
    cy.clickOnEntityMenuItem('rbac-user');
    cy.wait('@entitiesRequestAfterCreate');
    cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount + 1);
    cy.visit('/');
  });

  it('should delete last instance of RbacUser', () => {
    cy.intercept('GET', '/services/dataacm/api/rbac-users*').as('entitiesRequest');
    cy.intercept('GET', '/services/dataacm/api/rbac-users/*').as('dialogDeleteRequest');
    cy.intercept('DELETE', '/services/dataacm/api/rbac-users/*').as('deleteEntityRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('rbac-user');
    cy.wait('@entitiesRequest').then(({ request, response }) => {
      startingEntitiesCount = response.body.length;
      if (startingEntitiesCount > 0) {
        cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount);
        cy.get(entityDeleteButtonSelector).last().click({ force: true });
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('rbacUser').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click({ force: true });
        cy.wait('@deleteEntityRequest');
        cy.intercept('GET', '/services/dataacm/api/rbac-users*').as('entitiesRequestAfterDelete');
        cy.visit('/');
        cy.clickOnEntityMenuItem('rbac-user');
        cy.wait('@entitiesRequestAfterDelete');
        cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount - 1);
      }
      cy.visit('/');
    });
  });
});
