import axios from 'axios';
import { ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';
import { IRbacSecurityGroup, defaultValue } from 'app/shared/model/dataacm/rbac-security-group.model';

export const ACTION_TYPES = {
  FETCH_RBACSECURITYGROUP_LIST: 'rbacSecurityGroup/FETCH_RBACSECURITYGROUP_LIST',
  FETCH_RBACSECURITYGROUP: 'rbacSecurityGroup/FETCH_RBACSECURITYGROUP',
  CREATE_RBACSECURITYGROUP: 'rbacSecurityGroup/CREATE_RBACSECURITYGROUP',
  UPDATE_RBACSECURITYGROUP: 'rbacSecurityGroup/UPDATE_RBACSECURITYGROUP',
  PARTIAL_UPDATE_RBACSECURITYGROUP: 'rbacSecurityGroup/PARTIAL_UPDATE_RBACSECURITYGROUP',
  DELETE_RBACSECURITYGROUP: 'rbacSecurityGroup/DELETE_RBACSECURITYGROUP',
  RESET: 'rbacSecurityGroup/RESET',
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IRbacSecurityGroup>,
  entity: defaultValue,
  updating: false,
  totalItems: 0,
  updateSuccess: false,
};

export type RbacSecurityGroupState = Readonly<typeof initialState>;

// Reducer

export default (state: RbacSecurityGroupState = initialState, action): RbacSecurityGroupState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.FETCH_RBACSECURITYGROUP_LIST):
    case REQUEST(ACTION_TYPES.FETCH_RBACSECURITYGROUP):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true,
      };
    case REQUEST(ACTION_TYPES.CREATE_RBACSECURITYGROUP):
    case REQUEST(ACTION_TYPES.UPDATE_RBACSECURITYGROUP):
    case REQUEST(ACTION_TYPES.DELETE_RBACSECURITYGROUP):
    case REQUEST(ACTION_TYPES.PARTIAL_UPDATE_RBACSECURITYGROUP):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true,
      };
    case FAILURE(ACTION_TYPES.FETCH_RBACSECURITYGROUP_LIST):
    case FAILURE(ACTION_TYPES.FETCH_RBACSECURITYGROUP):
    case FAILURE(ACTION_TYPES.CREATE_RBACSECURITYGROUP):
    case FAILURE(ACTION_TYPES.UPDATE_RBACSECURITYGROUP):
    case FAILURE(ACTION_TYPES.PARTIAL_UPDATE_RBACSECURITYGROUP):
    case FAILURE(ACTION_TYPES.DELETE_RBACSECURITYGROUP):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload,
      };
    case SUCCESS(ACTION_TYPES.FETCH_RBACSECURITYGROUP_LIST):
      return {
        ...state,
        loading: false,
        entities: action.payload.data,
        totalItems: parseInt(action.payload.headers['x-total-count'], 10),
      };
    case SUCCESS(ACTION_TYPES.FETCH_RBACSECURITYGROUP):
      return {
        ...state,
        loading: false,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.CREATE_RBACSECURITYGROUP):
    case SUCCESS(ACTION_TYPES.UPDATE_RBACSECURITYGROUP):
    case SUCCESS(ACTION_TYPES.PARTIAL_UPDATE_RBACSECURITYGROUP):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.DELETE_RBACSECURITYGROUP):
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

const apiUrl = 'services/dataacm/api/rbac-security-groups';

// Actions

export const getEntities: ICrudGetAllAction<IRbacSecurityGroup> = (page, size, sort) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
  return {
    type: ACTION_TYPES.FETCH_RBACSECURITYGROUP_LIST,
    payload: axios.get<IRbacSecurityGroup>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`),
  };
};

export const getEntity: ICrudGetAction<IRbacSecurityGroup> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_RBACSECURITYGROUP,
    payload: axios.get<IRbacSecurityGroup>(requestUrl),
  };
};

export const createEntity: ICrudPutAction<IRbacSecurityGroup> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_RBACSECURITYGROUP,
    payload: axios.post(apiUrl, cleanEntity(entity)),
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<IRbacSecurityGroup> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_RBACSECURITYGROUP,
    payload: axios.put(`${apiUrl}/${entity.id}`, cleanEntity(entity)),
  });
  return result;
};

export const partialUpdate: ICrudPutAction<IRbacSecurityGroup> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.PARTIAL_UPDATE_RBACSECURITYGROUP,
    payload: axios.patch(`${apiUrl}/${entity.id}`, cleanEntity(entity)),
  });
  return result;
};

export const deleteEntity: ICrudDeleteAction<IRbacSecurityGroup> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_RBACSECURITYGROUP,
    payload: axios.delete(requestUrl),
  });
  dispatch(getEntities());
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET,
});
