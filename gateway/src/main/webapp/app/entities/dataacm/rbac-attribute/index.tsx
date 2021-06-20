import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import RbacAttribute from './rbac-attribute';
import RbacAttributeDetail from './rbac-attribute-detail';
import RbacAttributeUpdate from './rbac-attribute-update';
import RbacAttributeDeleteDialog from './rbac-attribute-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={RbacAttributeUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={RbacAttributeUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={RbacAttributeDetail} />
      <ErrorBoundaryRoute path={match.url} component={RbacAttribute} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={RbacAttributeDeleteDialog} />
  </>
);

export default Routes;
