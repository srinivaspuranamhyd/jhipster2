import { IRbacPermissionApproval } from 'app/shared/model/dataacm/rbac-permission-approval.model';
import { IRbacAttribute } from 'app/shared/model/dataacm/rbac-attribute.model';
import { IRbacUser } from 'app/shared/model/dataacm/rbac-user.model';

export interface IRbacPermission {
  id?: number;
  name?: string;
  desc?: string;
  approval?: IRbacPermissionApproval | null;
  attrs?: IRbacAttribute[] | null;
  user?: IRbacUser | null;
}

export const defaultValue: Readonly<IRbacPermission> = {};
