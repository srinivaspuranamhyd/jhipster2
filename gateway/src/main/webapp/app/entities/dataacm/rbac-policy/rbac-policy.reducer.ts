import axios from 'axios';
import { ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';
import { IRbacPolicy, defaultValue } from 'app/shared/model/dataacm/rbac-policy.model';

export const ACTION_TYPES = {
  FETCH_RBACPOLICY_LIST: 'rbacPolicy/FETCH_RBACPOLICY_LIST',
  FETCH_RBACPOLICY: 'rbacPolicy/FETCH_RBACPOLICY',
  CREATE_RBACPOLICY: 'rbacPolicy/CREATE_RBACPOLICY',
  UPDATE_RBACPOLICY: 'rbacPolicy/UPDATE_RBACPOLICY',
  PARTIAL_UPDATE_RBACPOLICY: 'rbacPolicy/PARTIAL_UPDATE_RBACPOLICY',
  DELETE_RBACPOLICY: 'rbacPolicy/DELETE_RBACPOLICY',
  RESET: 'rbacPolicy/RESET',
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IRbacPolicy>,
  entity: defaultValue,
  updating: false,
  totalItems: 0,
  updateSuccess: false,
};

export type RbacPolicyState = Readonly<typeof initialState>;

// Reducer

export default (state: RbacPolicyState = initialState, action): RbacPolicyState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.FETCH_RBACPOLICY_LIST):
    case REQUEST(ACTION_TYPES.FETCH_RBACPOLICY):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true,
      };
    case REQUEST(ACTION_TYPES.CREATE_RBACPOLICY):
    case REQUEST(ACTION_TYPES.UPDATE_RBACPOLICY):
    case REQUEST(ACTION_TYPES.DELETE_RBACPOLICY):
    case REQUEST(ACTION_TYPES.PARTIAL_UPDATE_RBACPOLICY):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true,
      };
    case FAILURE(ACTION_TYPES.FETCH_RBACPOLICY_LIST):
    case FAILURE(ACTION_TYPES.FETCH_RBACPOLICY):
    case FAILURE(ACTION_TYPES.CREATE_RBACPOLICY):
    case FAILURE(ACTION_TYPES.UPDATE_RBACPOLICY):
    case FAILURE(ACTION_TYPES.PARTIAL_UPDATE_RBACPOLICY):
    case FAILURE(ACTION_TYPES.DELETE_RBACPOLICY):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload,
      };
    case SUCCESS(ACTION_TYPES.FETCH_RBACPOLICY_LIST):
      return {
        ...state,
        loading: false,
        entities: action.payload.data,
        totalItems: parseInt(action.payload.headers['x-total-count'], 10),
      };
    case SUCCESS(ACTION_TYPES.FETCH_RBACPOLICY):
      return {
        ...state,
        loading: false,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.CREATE_RBACPOLICY):
    case SUCCESS(ACTION_TYPES.UPDATE_RBACPOLICY):
    case SUCCESS(ACTION_TYPES.PARTIAL_UPDATE_RBACPOLICY):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.DELETE_RBACPOLICY):
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

const apiUrl = 'services/dataacm/api/rbac-policies';

// Actions

export const getEntities: ICrudGetAllAction<IRbacPolicy> = (page, size, sort) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
  return {
    type: ACTION_TYPES.FETCH_RBACPOLICY_LIST,
    payload: axios.get<IRbacPolicy>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`),
  };
};

export const getEntity: ICrudGetAction<IRbacPolicy> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_RBACPOLICY,
    payload: axios.get<IRbacPolicy>(requestUrl),
  };
};

export const createEntity: ICrudPutAction<IRbacPolicy> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_RBACPOLICY,
    payload: axios.post(apiUrl, cleanEntity(entity)),
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<IRbacPolicy> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_RBACPOLICY,
    payload: axios.put(`${apiUrl}/${entity.id}`, cleanEntity(entity)),
  });
  return result;
};

export const partialUpdate: ICrudPutAction<IRbacPolicy> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.PARTIAL_UPDATE_RBACPOLICY,
    payload: axios.patch(`${apiUrl}/${entity.id}`, cleanEntity(entity)),
  });
  return result;
};

export const deleteEntity: ICrudDeleteAction<IRbacPolicy> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_RBACPOLICY,
    payload: axios.delete(requestUrl),
  });
  dispatch(getEntities());
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET,
});
