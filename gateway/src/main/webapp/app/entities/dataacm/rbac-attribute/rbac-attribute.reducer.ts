import axios from 'axios';
import { ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';
import { IRbacAttribute, defaultValue } from 'app/shared/model/dataacm/rbac-attribute.model';

export const ACTION_TYPES = {
  FETCH_RBACATTRIBUTE_LIST: 'rbacAttribute/FETCH_RBACATTRIBUTE_LIST',
  FETCH_RBACATTRIBUTE: 'rbacAttribute/FETCH_RBACATTRIBUTE',
  CREATE_RBACATTRIBUTE: 'rbacAttribute/CREATE_RBACATTRIBUTE',
  UPDATE_RBACATTRIBUTE: 'rbacAttribute/UPDATE_RBACATTRIBUTE',
  PARTIAL_UPDATE_RBACATTRIBUTE: 'rbacAttribute/PARTIAL_UPDATE_RBACATTRIBUTE',
  DELETE_RBACATTRIBUTE: 'rbacAttribute/DELETE_RBACATTRIBUTE',
  RESET: 'rbacAttribute/RESET',
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IRbacAttribute>,
  entity: defaultValue,
  updating: false,
  totalItems: 0,
  updateSuccess: false,
};

export type RbacAttributeState = Readonly<typeof initialState>;

// Reducer

export default (state: RbacAttributeState = initialState, action): RbacAttributeState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.FETCH_RBACATTRIBUTE_LIST):
    case REQUEST(ACTION_TYPES.FETCH_RBACATTRIBUTE):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true,
      };
    case REQUEST(ACTION_TYPES.CREATE_RBACATTRIBUTE):
    case REQUEST(ACTION_TYPES.UPDATE_RBACATTRIBUTE):
    case REQUEST(ACTION_TYPES.DELETE_RBACATTRIBUTE):
    case REQUEST(ACTION_TYPES.PARTIAL_UPDATE_RBACATTRIBUTE):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true,
      };
    case FAILURE(ACTION_TYPES.FETCH_RBACATTRIBUTE_LIST):
    case FAILURE(ACTION_TYPES.FETCH_RBACATTRIBUTE):
    case FAILURE(ACTION_TYPES.CREATE_RBACATTRIBUTE):
    case FAILURE(ACTION_TYPES.UPDATE_RBACATTRIBUTE):
    case FAILURE(ACTION_TYPES.PARTIAL_UPDATE_RBACATTRIBUTE):
    case FAILURE(ACTION_TYPES.DELETE_RBACATTRIBUTE):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload,
      };
    case SUCCESS(ACTION_TYPES.FETCH_RBACATTRIBUTE_LIST):
      return {
        ...state,
        loading: false,
        entities: action.payload.data,
        totalItems: parseInt(action.payload.headers['x-total-count'], 10),
      };
    case SUCCESS(ACTION_TYPES.FETCH_RBACATTRIBUTE):
      return {
        ...state,
        loading: false,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.CREATE_RBACATTRIBUTE):
    case SUCCESS(ACTION_TYPES.UPDATE_RBACATTRIBUTE):
    case SUCCESS(ACTION_TYPES.PARTIAL_UPDATE_RBACATTRIBUTE):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.DELETE_RBACATTRIBUTE):
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

const apiUrl = 'services/dataacm/api/rbac-attributes';

// Actions

export const getEntities: ICrudGetAllAction<IRbacAttribute> = (page, size, sort) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
  return {
    type: ACTION_TYPES.FETCH_RBACATTRIBUTE_LIST,
    payload: axios.get<IRbacAttribute>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`),
  };
};

export const getEntity: ICrudGetAction<IRbacAttribute> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_RBACATTRIBUTE,
    payload: axios.get<IRbacAttribute>(requestUrl),
  };
};

export const createEntity: ICrudPutAction<IRbacAttribute> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_RBACATTRIBUTE,
    payload: axios.post(apiUrl, cleanEntity(entity)),
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<IRbacAttribute> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_RBACATTRIBUTE,
    payload: axios.put(`${apiUrl}/${entity.id}`, cleanEntity(entity)),
  });
  return result;
};

export const partialUpdate: ICrudPutAction<IRbacAttribute> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.PARTIAL_UPDATE_RBACATTRIBUTE,
    payload: axios.patch(`${apiUrl}/${entity.id}`, cleanEntity(entity)),
  });
  return result;
};

export const deleteEntity: ICrudDeleteAction<IRbacAttribute> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_RBACATTRIBUTE,
    payload: axios.delete(requestUrl),
  });
  dispatch(getEntities());
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET,
});
