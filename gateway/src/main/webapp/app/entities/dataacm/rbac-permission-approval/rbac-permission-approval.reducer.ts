import axios from 'axios';
import { ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';
import { IRbacPermissionApproval, defaultValue } from 'app/shared/model/dataacm/rbac-permission-approval.model';

export const ACTION_TYPES = {
  FETCH_RBACPERMISSIONAPPROVAL_LIST: 'rbacPermissionApproval/FETCH_RBACPERMISSIONAPPROVAL_LIST',
  FETCH_RBACPERMISSIONAPPROVAL: 'rbacPermissionApproval/FETCH_RBACPERMISSIONAPPROVAL',
  CREATE_RBACPERMISSIONAPPROVAL: 'rbacPermissionApproval/CREATE_RBACPERMISSIONAPPROVAL',
  UPDATE_RBACPERMISSIONAPPROVAL: 'rbacPermissionApproval/UPDATE_RBACPERMISSIONAPPROVAL',
  PARTIAL_UPDATE_RBACPERMISSIONAPPROVAL: 'rbacPermissionApproval/PARTIAL_UPDATE_RBACPERMISSIONAPPROVAL',
  DELETE_RBACPERMISSIONAPPROVAL: 'rbacPermissionApproval/DELETE_RBACPERMISSIONAPPROVAL',
  RESET: 'rbacPermissionApproval/RESET',
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IRbacPermissionApproval>,
  entity: defaultValue,
  updating: false,
  totalItems: 0,
  updateSuccess: false,
};

export type RbacPermissionApprovalState = Readonly<typeof initialState>;

// Reducer

export default (state: RbacPermissionApprovalState = initialState, action): RbacPermissionApprovalState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.FETCH_RBACPERMISSIONAPPROVAL_LIST):
    case REQUEST(ACTION_TYPES.FETCH_RBACPERMISSIONAPPROVAL):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true,
      };
    case REQUEST(ACTION_TYPES.CREATE_RBACPERMISSIONAPPROVAL):
    case REQUEST(ACTION_TYPES.UPDATE_RBACPERMISSIONAPPROVAL):
    case REQUEST(ACTION_TYPES.DELETE_RBACPERMISSIONAPPROVAL):
    case REQUEST(ACTION_TYPES.PARTIAL_UPDATE_RBACPERMISSIONAPPROVAL):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true,
      };
    case FAILURE(ACTION_TYPES.FETCH_RBACPERMISSIONAPPROVAL_LIST):
    case FAILURE(ACTION_TYPES.FETCH_RBACPERMISSIONAPPROVAL):
    case FAILURE(ACTION_TYPES.CREATE_RBACPERMISSIONAPPROVAL):
    case FAILURE(ACTION_TYPES.UPDATE_RBACPERMISSIONAPPROVAL):
    case FAILURE(ACTION_TYPES.PARTIAL_UPDATE_RBACPERMISSIONAPPROVAL):
    case FAILURE(ACTION_TYPES.DELETE_RBACPERMISSIONAPPROVAL):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload,
      };
    case SUCCESS(ACTION_TYPES.FETCH_RBACPERMISSIONAPPROVAL_LIST):
      return {
        ...state,
        loading: false,
        entities: action.payload.data,
        totalItems: parseInt(action.payload.headers['x-total-count'], 10),
      };
    case SUCCESS(ACTION_TYPES.FETCH_RBACPERMISSIONAPPROVAL):
      return {
        ...state,
        loading: false,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.CREATE_RBACPERMISSIONAPPROVAL):
    case SUCCESS(ACTION_TYPES.UPDATE_RBACPERMISSIONAPPROVAL):
    case SUCCESS(ACTION_TYPES.PARTIAL_UPDATE_RBACPERMISSIONAPPROVAL):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.DELETE_RBACPERMISSIONAPPROVAL):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: {},
      };
    case ACTION_TYPES.RESET:
      return {
        ...initialState,
      };
    default:
      return state;
  }
};

const apiUrl = 'services/dataacm/api/rbac-permission-approvals';

// Actions

export const getEntities: ICrudGetAllAction<IRbacPermissionApproval> = (page, size, sort) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
  return {
    type: ACTION_TYPES.FETCH_RBACPERMISSIONAPPROVAL_LIST,
    payload: axios.get<IRbacPermissionApproval>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`),
  };
};

export const getEntity: ICrudGetAction<IRbacPermissionApproval> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_RBACPERMISSIONAPPROVAL,
    payload: axios.get<IRbacPermissionApproval>(requestUrl),
  };
};

export const createEntity: ICrudPutAction<IRbacPermissionApproval> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_RBACPERMISSIONAPPROVAL,
    payload: axios.post(apiUrl, cleanEntity(entity)),
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<IRbacPermissionApproval> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_RBACPERMISSIONAPPROVAL,
    payload: axios.put(`${apiUrl}/${entity.id}`, cleanEntity(entity)),
  });
  return result;
};

export const partialUpdate: ICrudPutAction<IRbacPermissionApproval> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.PARTIAL_UPDATE_RBACPERMISSIONAPPROVAL,
    payload: axios.patch(`${apiUrl}/${entity.id}`, cleanEntity(entity)),
  });
  return result;
};

export const deleteEntity: ICrudDeleteAction<IRbacPermissionApproval> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_RBACPERMISSIONAPPROVAL,
    payload: axios.delete(requestUrl),
  });
  dispatch(getEntities());
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET,
});
