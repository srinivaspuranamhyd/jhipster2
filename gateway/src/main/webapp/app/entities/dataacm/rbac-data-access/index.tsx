import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import RbacDataAccess from './rbac-data-access';
import RbacDataAccessDetail from './rbac-data-access-detail';
import RbacDataAccessUpdate from './rbac-data-access-update';
import RbacDataAccessDeleteDialog from './rbac-data-access-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={RbacDataAccessUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={RbacDataAccessUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={RbacDataAccessDetail} />
      <ErrorBoundaryRoute path={match.url} component={RbacDataAccess} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={RbacDataAccessDeleteDialog} />
  </>
);

export default Routes;
