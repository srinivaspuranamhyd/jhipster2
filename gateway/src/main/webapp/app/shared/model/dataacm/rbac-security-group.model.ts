import { IRbacDataAccess } from 'app/shared/model/dataacm/rbac-data-access.model';
import { IRbacUser } from 'app/shared/model/dataacm/rbac-user.model';

export interface IRbacSecurityGroup {
  id?: number;
  name?: string;
  desc?: string;
  dataTopics?: IRbacDataAccess[] | null;
  users?: IRbacUser[] | null;
}

export const defaultValue: Readonly<IRbacSecurityGroup> = {};
