import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import RbacUser from './rbac-user';
import RbacUserDetail from './rbac-user-detail';
import RbacUserUpdate from './rbac-user-update';
import RbacUserDeleteDialog from './rbac-user-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={RbacUserUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={RbacUserUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={RbacUserDetail} />
      <ErrorBoundaryRoute path={match.url} component={RbacUser} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={RbacUserDeleteDialog} />
  </>
);

export default Routes;
