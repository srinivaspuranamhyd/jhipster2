import { IRbacAttributeVal } from 'app/shared/model/dataacm/rbac-attribute-val.model';
import { IRbacPolicy } from 'app/shared/model/dataacm/rbac-policy.model';
import { IRbacPermission } from 'app/shared/model/dataacm/rbac-permission.model';

export interface IRbacAttribute {
  id?: number;
  name?: string;
  type?: string;
  attrvals?: IRbacAttributeVal[] | null;
  rbacPolicy?: IRbacPolicy | null;
  rbacPermission?: IRbacPermission | null;
}

export const defaultValue: Readonly<IRbacAttribute> = {};
