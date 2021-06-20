import { IRbacPolicy } from 'app/shared/model/dataacm/rbac-policy.model';
import { IRbacPermission } from 'app/shared/model/dataacm/rbac-permission.model';
import { IRbacSecurityGroup } from 'app/shared/model/dataacm/rbac-security-group.model';
import { RbacUserStatus } from 'app/shared/model/enumerations/rbac-user-status.model';

export interface IRbacUser {
  id?: number;
  onebankId?: string;
  lanId?: string;
  email?: string;
  status?: RbacUserStatus;
  department?: string;
  country?: string;
  managerId?: string | null;
  policies?: IRbacPolicy[] | null;
  permissions?: IRbacPermission[] | null;
  groups?: IRbacSecurityGroup[] | null;
}

export const defaultValue: Readonly<IRbacUser> = {};
