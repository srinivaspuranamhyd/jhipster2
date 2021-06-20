import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import RbacPolicy from './rbac-policy';
import RbacPolicyDetail from './rbac-policy-detail';
import RbacPolicyUpdate from './rbac-policy-update';
import RbacPolicyDeleteDialog from './rbac-policy-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={RbacPolicyUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={RbacPolicyUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={RbacPolicyDetail} />
      <ErrorBoundaryRoute path={match.url} component={RbacPolicy} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={RbacPolicyDeleteDialog} />
  </>
);

export default Routes;
