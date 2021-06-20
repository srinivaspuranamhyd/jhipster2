import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import RbacSecurityGroup from './rbac-security-group';
import RbacSecurityGroupDetail from './rbac-security-group-detail';
import RbacSecurityGroupUpdate from './rbac-security-group-update';
import RbacSecurityGroupDeleteDialog from './rbac-security-group-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={RbacSecurityGroupUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={RbacSecurityGroupUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={RbacSecurityGroupDetail} />
      <ErrorBoundaryRoute path={match.url} component={RbacSecurityGroup} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={RbacSecurityGroupDeleteDialog} />
  </>
);

export default Routes;
