import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import RbacPermission from './rbac-permission';
import RbacPermissionDetail from './rbac-permission-detail';
import RbacPermissionUpdate from './rbac-permission-update';
import RbacPermissionDeleteDialog from './rbac-permission-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={RbacPermissionUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={RbacPermissionUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={RbacPermissionDetail} />
      <ErrorBoundaryRoute path={match.url} component={RbacPermission} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={RbacPermissionDeleteDialog} />
  </>
);

export default Routes;
