import { IRbacAttribute } from 'app/shared/model/dataacm/rbac-attribute.model';

export interface IRbacAttributeVal {
  id?: number;
  value?: string;
  attr?: IRbacAttribute | null;
}

export const defaultValue: Readonly<IRbacAttributeVal> = {};
