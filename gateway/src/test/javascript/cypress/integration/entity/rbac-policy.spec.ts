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

describe('RbacPolicy e2e test', () => {
  let startingEntitiesCount = 0;

  beforeEach(() => {
    cy.getOauth2Data();
    cy.get('@oauth2Data').then(oauth2Data => {
      cy.oauthLogin(oauth2Data, Cypress.env('E2E_USERNAME') || 'admin', Cypress.env('E2E_PASSWORD') || 'admin');
    });
    cy.intercept('GET', '/services/dataacm/api/rbac-policies*').as('entitiesRequest');
    cy.visit('');
    cy.clickOnEntityMenuItem('rbac-policy');
    cy.wait('@entitiesRequest').then(({ request, response }) => (startingEntitiesCount = response.body.length));
    cy.visit('/');
  });

  afterEach(() => {
    cy.get('@oauth2Data').then(oauth2Data => {
      cy.oauthLogout(oauth2Data);
    });
    cy.clearCache();
  });

  it('should load RbacPolicies', () => {
    cy.intercept('GET', '/services/dataacm/api/rbac-policies*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('rbac-policy');
    cy.wait('@entitiesRequest');
    cy.getEntityHeading('RbacPolicy').should('exist');
    if (startingEntitiesCount === 0) {
      cy.get(entityTableSelector).should('not.exist');
    } else {
      cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount);
    }
    cy.visit('/');
  });

  it('should load details RbacPolicy page', () => {
    cy.intercept('GET', '/services/dataacm/api/rbac-policies*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('rbac-policy');
    cy.wait('@entitiesRequest');
    if (startingEntitiesCount > 0) {
      cy.get(entityDetailsButtonSelector).first().click({ force: true });
      cy.getEntityDetailsHeading('rbacPolicy');
      cy.get(entityDetailsBackButtonSelector).should('exist');
    }
    cy.visit('/');
  });

  it('should load create RbacPolicy page', () => {
    cy.intercept('GET', '/services/dataacm/api/rbac-policies*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('rbac-policy');
    cy.wait('@entitiesRequest');
    cy.get(entityCreateButtonSelector).click({ force: true });
    cy.getEntityCreateUpdateHeading('RbacPolicy');
    cy.get(entityCreateSaveButtonSelector).should('exist');
    cy.visit('/');
  });

  it('should load edit RbacPolicy page', () => {
    cy.intercept('GET', '/services/dataacm/api/rbac-policies*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('rbac-policy');
    cy.wait('@entitiesRequest');
    if (startingEntitiesCount > 0) {
      cy.get(entityEditButtonSelector).first().click({ force: true });
      cy.getEntityCreateUpdateHeading('RbacPolicy');
      cy.get(entityCreateSaveButtonSelector).should('exist');
    }
    cy.visit('/');
  });

  it('should create an instance of RbacPolicy', () => {
    cy.intercept('GET', '/services/dataacm/api/rbac-policies*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('rbac-policy');
    cy.wait('@entitiesRequest').then(({ request, response }) => (startingEntitiesCount = response.body.length));
    cy.get(entityCreateButtonSelector).click({ force: true });
    cy.getEntityCreateUpdateHeading('RbacPolicy');

    cy.get(`[data-cy="name"]`).type('program', { force: true }).invoke('val').should('match', new RegExp('program'));

    cy.get(`[data-cy="desc"]`).type('Gardens virtual', { force: true }).invoke('val').should('match', new RegExp('Gardens virtual'));

    cy.setFieldSelectToLastOfEntity('user');

    cy.get(entityCreateSaveButtonSelector).click({ force: true });
    cy.scrollTo('top', { ensureScrollable: false });
    cy.get(entityCreateSaveButtonSelector).should('not.exist');
    cy.intercept('GET', '/services/dataacm/api/rbac-policies*').as('entitiesRequestAfterCreate');
    cy.visit('/');
    cy.clickOnEntityMenuItem('rbac-policy');
    cy.wait('@entitiesRequestAfterCreate');
    cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount + 1);
    cy.visit('/');
  });

  it('should delete last instance of RbacPolicy', () => {
    cy.intercept('GET', '/services/dataacm/api/rbac-policies*').as('entitiesRequest');
    cy.intercept('GET', '/services/dataacm/api/rbac-policies/*').as('dialogDeleteRequest');
    cy.intercept('DELETE', '/services/dataacm/api/rbac-policies/*').as('deleteEntityRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('rbac-policy');
    cy.wait('@entitiesRequest').then(({ request, response }) => {
      startingEntitiesCount = response.body.length;
      if (startingEntitiesCount > 0) {
        cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount);
        cy.get(entityDeleteButtonSelector).last().click({ force: true });
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('rbacPolicy').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click({ force: true });
        cy.wait('@deleteEntityRequest');
        cy.intercept('GET', '/services/dataacm/api/rbac-policies*').as('entitiesRequestAfterDelete');
        cy.visit('/');
        cy.clickOnEntityMenuItem('rbac-policy');
        cy.wait('@entitiesRequestAfterDelete');
        cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount - 1);
      }
      cy.visit('/');
    });
  });
});
