import axios from 'axios';
import { ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';
import { IRbacPermission, defaultValue } from 'app/shared/model/dataacm/rbac-permission.model';

export const ACTION_TYPES = {
  FETCH_RBACPERMISSION_LIST: 'rbacPermission/FETCH_RBACPERMISSION_LIST',
  FETCH_RBACPERMISSION: 'rbacPermission/FETCH_RBACPERMISSION',
  CREATE_RBACPERMISSION: 'rbacPermission/CREATE_RBACPERMISSION',
  UPDATE_RBACPERMISSION: 'rbacPermission/UPDATE_RBACPERMISSION',
  PARTIAL_UPDATE_RBACPERMISSION: 'rbacPermission/PARTIAL_UPDATE_RBACPERMISSION',
  DELETE_RBACPERMISSION: 'rbacPermission/DELETE_RBACPERMISSION',
  RESET: 'rbacPermission/RESET',
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IRbacPermission>,
  entity: defaultValue,
  updating: false,
  totalItems: 0,
  updateSuccess: false,
};

export type RbacPermissionState = Readonly<typeof initialState>;

// Reducer

export default (state: RbacPermissionState = initialState, action): RbacPermissionState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.FETCH_RBACPERMISSION_LIST):
    case REQUEST(ACTION_TYPES.FETCH_RBACPERMISSION):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true,
      };
    case REQUEST(ACTION_TYPES.CREATE_RBACPERMISSION):
    case REQUEST(ACTION_TYPES.UPDATE_RBACPERMISSION):
    case REQUEST(ACTION_TYPES.DELETE_RBACPERMISSION):
    case REQUEST(ACTION_TYPES.PARTIAL_UPDATE_RBACPERMISSION):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true,
      };
    case FAILURE(ACTION_TYPES.FETCH_RBACPERMISSION_LIST):
    case FAILURE(ACTION_TYPES.FETCH_RBACPERMISSION):
    case FAILURE(ACTION_TYPES.CREATE_RBACPERMISSION):
    case FAILURE(ACTION_TYPES.UPDATE_RBACPERMISSION):
    case FAILURE(ACTION_TYPES.PARTIAL_UPDATE_RBACPERMISSION):
    case FAILURE(ACTION_TYPES.DELETE_RBACPERMISSION):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload,
      };
    case SUCCESS(ACTION_TYPES.FETCH_RBACPERMISSION_LIST):
      return {
        ...state,
        loading: false,
        entities: action.payload.data,
        totalItems: parseInt(action.payload.headers['x-total-count'], 10),
      };
    case SUCCESS(ACTION_TYPES.FETCH_RBACPERMISSION):
      return {
        ...state,
        loading: false,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.CREATE_RBACPERMISSION):
    case SUCCESS(ACTION_TYPES.UPDATE_RBACPERMISSION):
    case SUCCESS(ACTION_TYPES.PARTIAL_UPDATE_RBACPERMISSION):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.DELETE_RBACPERMISSION):
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

const apiUrl = 'services/dataacm/api/rbac-permissions';

// Actions

export const getEntities: ICrudGetAllAction<IRbacPermission> = (page, size, sort) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
  return {
    type: ACTION_TYPES.FETCH_RBACPERMISSION_LIST,
    payload: axios.get<IRbacPermission>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`),
  };
};

export const getEntity: ICrudGetAction<IRbacPermission> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_RBACPERMISSION,
    payload: axios.get<IRbacPermission>(requestUrl),
  };
};

export const createEntity: ICrudPutAction<IRbacPermission> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_RBACPERMISSION,
    payload: axios.post(apiUrl, cleanEntity(entity)),
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<IRbacPermission> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_RBACPERMISSION,
    payload: axios.put(`${apiUrl}/${entity.id}`, cleanEntity(entity)),
  });
  return result;
};

export const partialUpdate: ICrudPutAction<IRbacPermission> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.PARTIAL_UPDATE_RBACPERMISSION,
    payload: axios.patch(`${apiUrl}/${entity.id}`, cleanEntity(entity)),
  });
  return result;
};

export const deleteEntity: ICrudDeleteAction<IRbacPermission> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_RBACPERMISSION,
    payload: axios.delete(requestUrl),
  });
  dispatch(getEntities());
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET,
});
