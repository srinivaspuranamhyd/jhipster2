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

describe('RbacAttribute e2e test', () => {
  let startingEntitiesCount = 0;

  beforeEach(() => {
    cy.getOauth2Data();
    cy.get('@oauth2Data').then(oauth2Data => {
      cy.oauthLogin(oauth2Data, Cypress.env('E2E_USERNAME') || 'admin', Cypress.env('E2E_PASSWORD') || 'admin');
    });
    cy.intercept('GET', '/services/dataacm/api/rbac-attributes*').as('entitiesRequest');
    cy.visit('');
    cy.clickOnEntityMenuItem('rbac-attribute');
    cy.wait('@entitiesRequest').then(({ request, response }) => (startingEntitiesCount = response.body.length));
    cy.visit('/');
  });

  afterEach(() => {
    cy.get('@oauth2Data').then(oauth2Data => {
      cy.oauthLogout(oauth2Data);
    });
    cy.clearCache();
  });

  it('should load RbacAttributes', () => {
    cy.intercept('GET', '/services/dataacm/api/rbac-attributes*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('rbac-attribute');
    cy.wait('@entitiesRequest');
    cy.getEntityHeading('RbacAttribute').should('exist');
    if (startingEntitiesCount === 0) {
      cy.get(entityTableSelector).should('not.exist');
    } else {
      cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount);
    }
    cy.visit('/');
  });

  it('should load details RbacAttribute page', () => {
    cy.intercept('GET', '/services/dataacm/api/rbac-attributes*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('rbac-attribute');
    cy.wait('@entitiesRequest');
    if (startingEntitiesCount > 0) {
      cy.get(entityDetailsButtonSelector).first().click({ force: true });
      cy.getEntityDetailsHeading('rbacAttribute');
      cy.get(entityDetailsBackButtonSelector).should('exist');
    }
    cy.visit('/');
  });

  it('should load create RbacAttribute page', () => {
    cy.intercept('GET', '/services/dataacm/api/rbac-attributes*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('rbac-attribute');
    cy.wait('@entitiesRequest');
    cy.get(entityCreateButtonSelector).click({ force: true });
    cy.getEntityCreateUpdateHeading('RbacAttribute');
    cy.get(entityCreateSaveButtonSelector).should('exist');
    cy.visit('/');
  });

  it('should load edit RbacAttribute page', () => {
    cy.intercept('GET', '/services/dataacm/api/rbac-attributes*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('rbac-attribute');
    cy.wait('@entitiesRequest');
    if (startingEntitiesCount > 0) {
      cy.get(entityEditButtonSelector).first().click({ force: true });
      cy.getEntityCreateUpdateHeading('RbacAttribute');
      cy.get(entityCreateSaveButtonSelector).should('exist');
    }
    cy.visit('/');
  });

  it('should create an instance of RbacAttribute', () => {
    cy.intercept('GET', '/services/dataacm/api/rbac-attributes*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('rbac-attribute');
    cy.wait('@entitiesRequest').then(({ request, response }) => (startingEntitiesCount = response.body.length));
    cy.get(entityCreateButtonSelector).click({ force: true });
    cy.getEntityCreateUpdateHeading('RbacAttribute');

    cy.get(`[data-cy="name"]`).type('Cross-group', { force: true }).invoke('val').should('match', new RegExp('Cross-group'));

    cy.get(`[data-cy="type"]`)
      .type('schemas Cross-platform up', { force: true })
      .invoke('val')
      .should('match', new RegExp('schemas Cross-platform up'));

    cy.setFieldSelectToLastOfEntity('rbacPolicy');

    cy.setFieldSelectToLastOfEntity('rbacPermission');

    cy.get(entityCreateSaveButtonSelector).click({ force: true });
    cy.scrollTo('top', { ensureScrollable: false });
    cy.get(entityCreateSaveButtonSelector).should('not.exist');
    cy.intercept('GET', '/services/dataacm/api/rbac-attributes*').as('entitiesRequestAfterCreate');
    cy.visit('/');
    cy.clickOnEntityMenuItem('rbac-attribute');
    cy.wait('@entitiesRequestAfterCreate');
    cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount + 1);
    cy.visit('/');
  });

  it('should delete last instance of RbacAttribute', () => {
    cy.intercept('GET', '/services/dataacm/api/rbac-attributes*').as('entitiesRequest');
    cy.intercept('GET', '/services/dataacm/api/rbac-attributes/*').as('dialogDeleteRequest');
    cy.intercept('DELETE', '/services/dataacm/api/rbac-attributes/*').as('deleteEntityRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('rbac-attribute');
    cy.wait('@entitiesRequest').then(({ request, response }) => {
      startingEntitiesCount = response.body.length;
      if (startingEntitiesCount > 0) {
        cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount);
        cy.get(entityDeleteButtonSelector).last().click({ force: true });
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('rbacAttribute').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click({ force: true });
        cy.wait('@deleteEntityRequest');
        cy.intercept('GET', '/services/dataacm/api/rbac-attributes*').as('entitiesRequestAfterDelete');
        cy.visit('/');
        cy.clickOnEntityMenuItem('rbac-attribute');
        cy.wait('@entitiesRequestAfterDelete');
        cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount - 1);
      }
      cy.visit('/');
    });
  });
});
