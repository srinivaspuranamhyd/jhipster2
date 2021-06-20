import React from 'react';
import { Switch } from 'react-router-dom';

// eslint-disable-next-line @typescript-eslint/no-unused-vars
import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import RbacAttribute from './dataacm/rbac-attribute';
import RbacAttributeVal from './dataacm/rbac-attribute-val';
import RbacDataAccess from './dataacm/rbac-data-access';
import RbacPermission from './dataacm/rbac-permission';
import RbacPermissionApproval from './dataacm/rbac-permission-approval';
import RbacPolicy from './dataacm/rbac-policy';
import RbacSecurityGroup from './dataacm/rbac-security-group';
import RbacUser from './dataacm/rbac-user';
/* jhipster-needle-add-route-import - JHipster will add routes here */

const Routes = ({ match }) => (
  <div>
    <Switch>
      {/* prettier-ignore */}
      <ErrorBoundaryRoute path={`${match.url}rbac-attribute`} component={RbacAttribute} />
      <ErrorBoundaryRoute path={`${match.url}rbac-attribute-val`} component={RbacAttributeVal} />
      <ErrorBoundaryRoute path={`${match.url}rbac-data-access`} component={RbacDataAccess} />
      <ErrorBoundaryRoute path={`${match.url}rbac-permission`} component={RbacPermission} />
      <ErrorBoundaryRoute path={`${match.url}rbac-permission-approval`} component={RbacPermissionApproval} />
      <ErrorBoundaryRoute path={`${match.url}rbac-policy`} component={RbacPolicy} />
      <ErrorBoundaryRoute path={`${match.url}rbac-security-group`} component={RbacSecurityGroup} />
      <ErrorBoundaryRoute path={`${match.url}rbac-user`} component={RbacUser} />
      {/* jhipster-needle-add-route-path - JHipster will add routes here */}
    </Switch>
  </div>
);

export default Routes;
