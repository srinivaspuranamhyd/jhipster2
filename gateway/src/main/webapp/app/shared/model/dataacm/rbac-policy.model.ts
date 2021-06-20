import { IRbacAttribute } from 'app/shared/model/dataacm/rbac-attribute.model';
import { IRbacUser } from 'app/shared/model/dataacm/rbac-user.model';

export interface IRbacPolicy {
  id?: number;
  name?: string;
  desc?: string;
  attrs?: IRbacAttribute[] | null;
  user?: IRbacUser | null;
}

export const defaultValue: Readonly<IRbacPolicy> = {};
