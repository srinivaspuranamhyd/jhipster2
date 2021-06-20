import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import RbacPermissionApproval from './rbac-permission-approval';
import RbacPermissionApprovalDetail from './rbac-permission-approval-detail';
import RbacPermissionApprovalUpdate from './rbac-permission-approval-update';
import RbacPermissionApprovalDeleteDialog from './rbac-permission-approval-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={RbacPermissionApprovalUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={RbacPermissionApprovalUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={RbacPermissionApprovalDetail} />
      <ErrorBoundaryRoute path={match.url} component={RbacPermissionApproval} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={RbacPermissionApprovalDeleteDialog} />
  </>
);

export default Routes;
