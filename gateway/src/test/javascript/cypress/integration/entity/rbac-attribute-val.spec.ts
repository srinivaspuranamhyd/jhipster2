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

describe('RbacAttributeVal e2e test', () => {
  let startingEntitiesCount = 0;

  beforeEach(() => {
    cy.getOauth2Data();
    cy.get('@oauth2Data').then(oauth2Data => {
      cy.oauthLogin(oauth2Data, Cypress.env('E2E_USERNAME') || 'admin', Cypress.env('E2E_PASSWORD') || 'admin');
    });
    cy.intercept('GET', '/services/dataacm/api/rbac-attribute-vals*').as('entitiesRequest');
    cy.visit('');
    cy.clickOnEntityMenuItem('rbac-attribute-val');
    cy.wait('@entitiesRequest').then(({ request, response }) => (startingEntitiesCount = response.body.length));
    cy.visit('/');
  });

  afterEach(() => {
    cy.get('@oauth2Data').then(oauth2Data => {
      cy.oauthLogout(oauth2Data);
    });
    cy.clearCache();
  });

  it('should load RbacAttributeVals', () => {
    cy.intercept('GET', '/services/dataacm/api/rbac-attribute-vals*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('rbac-attribute-val');
    cy.wait('@entitiesRequest');
    cy.getEntityHeading('RbacAttributeVal').should('exist');
    if (startingEntitiesCount === 0) {
      cy.get(entityTableSelector).should('not.exist');
    } else {
      cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount);
    }
    cy.visit('/');
  });

  it('should load details RbacAttributeVal page', () => {
    cy.intercept('GET', '/services/dataacm/api/rbac-attribute-vals*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('rbac-attribute-val');
    cy.wait('@entitiesRequest');
    if (startingEntitiesCount > 0) {
      cy.get(entityDetailsButtonSelector).first().click({ force: true });
      cy.getEntityDetailsHeading('rbacAttributeVal');
      cy.get(entityDetailsBackButtonSelector).should('exist');
    }
    cy.visit('/');
  });

  it('should load create RbacAttributeVal page', () => {
    cy.intercept('GET', '/services/dataacm/api/rbac-attribute-vals*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('rbac-attribute-val');
    cy.wait('@entitiesRequest');
    cy.get(entityCreateButtonSelector).click({ force: true });
    cy.getEntityCreateUpdateHeading('RbacAttributeVal');
    cy.get(entityCreateSaveButtonSelector).should('exist');
    cy.visit('/');
  });

  it('should load edit RbacAttributeVal page', () => {
    cy.intercept('GET', '/services/dataacm/api/rbac-attribute-vals*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('rbac-attribute-val');
    cy.wait('@entitiesRequest');
    if (startingEntitiesCount > 0) {
      cy.get(entityEditButtonSelector).first().click({ force: true });
      cy.getEntityCreateUpdateHeading('RbacAttributeVal');
      cy.get(entityCreateSaveButtonSelector).should('exist');
    }
    cy.visit('/');
  });

  it('should create an instance of RbacAttributeVal', () => {
    cy.intercept('GET', '/services/dataacm/api/rbac-attribute-vals*').as('entitiesRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('rbac-attribute-val');
    cy.wait('@entitiesRequest').then(({ request, response }) => (startingEntitiesCount = response.body.length));
    cy.get(entityCreateButtonSelector).click({ force: true });
    cy.getEntityCreateUpdateHeading('RbacAttributeVal');

    cy.get(`[data-cy="value"]`).type('Interactions', { force: true }).invoke('val').should('match', new RegExp('Interactions'));

    cy.setFieldSelectToLastOfEntity('attr');

    cy.get(entityCreateSaveButtonSelector).click({ force: true });
    cy.scrollTo('top', { ensureScrollable: false });
    cy.get(entityCreateSaveButtonSelector).should('not.exist');
    cy.intercept('GET', '/services/dataacm/api/rbac-attribute-vals*').as('entitiesRequestAfterCreate');
    cy.visit('/');
    cy.clickOnEntityMenuItem('rbac-attribute-val');
    cy.wait('@entitiesRequestAfterCreate');
    cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount + 1);
    cy.visit('/');
  });

  it('should delete last instance of RbacAttributeVal', () => {
    cy.intercept('GET', '/services/dataacm/api/rbac-attribute-vals*').as('entitiesRequest');
    cy.intercept('GET', '/services/dataacm/api/rbac-attribute-vals/*').as('dialogDeleteRequest');
    cy.intercept('DELETE', '/services/dataacm/api/rbac-attribute-vals/*').as('deleteEntityRequest');
    cy.visit('/');
    cy.clickOnEntityMenuItem('rbac-attribute-val');
    cy.wait('@entitiesRequest').then(({ request, response }) => {
      startingEntitiesCount = response.body.length;
      if (startingEntitiesCount > 0) {
        cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount);
        cy.get(entityDeleteButtonSelector).last().click({ force: true });
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('rbacAttributeVal').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click({ force: true });
        cy.wait('@deleteEntityRequest');
        cy.intercept('GET', '/services/dataacm/api/rbac-attribute-vals*').as('entitiesRequestAfterDelete');
        cy.visit('/');
        cy.clickOnEntityMenuItem('rbac-attribute-val');
        cy.wait('@entitiesRequestAfterDelete');
        cy.get(entityTableSelector).should('have.lengthOf', startingEntitiesCount - 1);
      }
      cy.visit('/');
    });
  });
});
