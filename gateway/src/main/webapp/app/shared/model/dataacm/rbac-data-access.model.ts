import { IRbacSecurityGroup } from 'app/shared/model/dataacm/rbac-security-group.model';

export interface IRbacDataAccess {
  id?: number;
  name?: string;
  desc?: string;
  groups?: IRbacSecurityGroup[] | null;
}

export const defaultValue: Readonly<IRbacDataAccess> = {};
