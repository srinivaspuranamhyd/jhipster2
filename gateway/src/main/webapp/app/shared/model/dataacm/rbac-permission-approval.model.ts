import { IRbacPermission } from 'app/shared/model/dataacm/rbac-permission.model';
import { RbacPermissionApprovalStatus } from 'app/shared/model/enumerations/rbac-permission-approval-status.model';

export interface IRbacPermissionApproval {
  id?: number;
  desc?: string;
  approverEmail?: string;
  status?: RbacPermissionApprovalStatus;
  permission?: IRbacPermission | null;
}

export const defaultValue: Readonly<IRbacPermissionApproval> = {};
