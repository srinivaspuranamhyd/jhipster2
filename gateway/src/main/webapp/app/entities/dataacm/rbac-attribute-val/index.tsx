import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import RbacAttributeVal from './rbac-attribute-val';
import RbacAttributeValDetail from './rbac-attribute-val-detail';
import RbacAttributeValUpdate from './rbac-attribute-val-update';
import RbacAttributeValDeleteDialog from './rbac-attribute-val-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={RbacAttributeValUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={RbacAttributeValUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={RbacAttributeValDetail} />
      <ErrorBoundaryRoute path={match.url} component={RbacAttributeVal} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={RbacAttributeValDeleteDialog} />
  </>
);

export default Routes;
