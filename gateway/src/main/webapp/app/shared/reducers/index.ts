import { combineReducers } from 'redux';
import { loadingBarReducer as loadingBar } from 'react-redux-loading-bar';

import locale, { LocaleState } from './locale';
import authentication, { AuthenticationState } from './authentication';
import applicationProfile, { ApplicationProfileState } from './application-profile';

import administration, { AdministrationState } from 'app/modules/administration/administration.reducer';
import userManagement, { UserManagementState } from './user-management';
// prettier-ignore
import rbacAttribute, {
  RbacAttributeState
} from 'app/entities/dataacm/rbac-attribute/rbac-attribute.reducer';
// prettier-ignore
import rbacAttributeVal, {
  RbacAttributeValState
} from 'app/entities/dataacm/rbac-attribute-val/rbac-attribute-val.reducer';
// prettier-ignore
import rbacDataAccess, {
  RbacDataAccessState
} from 'app/entities/dataacm/rbac-data-access/rbac-data-access.reducer';
// prettier-ignore
import rbacPermission, {
  RbacPermissionState
} from 'app/entities/dataacm/rbac-permission/rbac-permission.reducer';
// prettier-ignore
import rbacPermissionApproval, {
  RbacPermissionApprovalState
} from 'app/entities/dataacm/rbac-permission-approval/rbac-permission-approval.reducer';
// prettier-ignore
import rbacPolicy, {
  RbacPolicyState
} from 'app/entities/dataacm/rbac-policy/rbac-policy.reducer';
// prettier-ignore
import rbacSecurityGroup, {
  RbacSecurityGroupState
} from 'app/entities/dataacm/rbac-security-group/rbac-security-group.reducer';
// prettier-ignore
import rbacUser, {
  RbacUserState
} from 'app/entities/dataacm/rbac-user/rbac-user.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

export interface IRootState {
  readonly authentication: AuthenticationState;
  readonly locale: LocaleState;
  readonly applicationProfile: ApplicationProfileState;
  readonly administration: AdministrationState;
  readonly userManagement: UserManagementState;
  readonly rbacAttribute: RbacAttributeState;
  readonly rbacAttributeVal: RbacAttributeValState;
  readonly rbacDataAccess: RbacDataAccessState;
  readonly rbacPermission: RbacPermissionState;
  readonly rbacPermissionApproval: RbacPermissionApprovalState;
  readonly rbacPolicy: RbacPolicyState;
  readonly rbacSecurityGroup: RbacSecurityGroupState;
  readonly rbacUser: RbacUserState;
  /* jhipster-needle-add-reducer-type - JHipster will add reducer type here */
  readonly loadingBar: any;
}

const rootReducer = combineReducers<IRootState>({
  authentication,
  locale,
  applicationProfile,
  administration,
  userManagement,
  rbacAttribute,
  rbacAttributeVal,
  rbacDataAccess,
  rbacPermission,
  rbacPermissionApproval,
  rbacPolicy,
  rbacSecurityGroup,
  rbacUser,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
  loadingBar,
});

export default rootReducer;
