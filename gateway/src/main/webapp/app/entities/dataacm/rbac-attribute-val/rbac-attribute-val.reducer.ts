import axios from 'axios';
import { ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';
import { IRbacAttributeVal, defaultValue } from 'app/shared/model/dataacm/rbac-attribute-val.model';

export const ACTION_TYPES = {
  FETCH_RBACATTRIBUTEVAL_LIST: 'rbacAttributeVal/FETCH_RBACATTRIBUTEVAL_LIST',
  FETCH_RBACATTRIBUTEVAL: 'rbacAttributeVal/FETCH_RBACATTRIBUTEVAL',
  CREATE_RBACATTRIBUTEVAL: 'rbacAttributeVal/CREATE_RBACATTRIBUTEVAL',
  UPDATE_RBACATTRIBUTEVAL: 'rbacAttributeVal/UPDATE_RBACATTRIBUTEVAL',
  PARTIAL_UPDATE_RBACATTRIBUTEVAL: 'rbacAttributeVal/PARTIAL_UPDATE_RBACATTRIBUTEVAL',
  DELETE_RBACATTRIBUTEVAL: 'rbacAttributeVal/DELETE_RBACATTRIBUTEVAL',
  RESET: 'rbacAttributeVal/RESET',
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IRbacAttributeVal>,
  entity: defaultValue,
  updating: false,
  totalItems: 0,
  updateSuccess: false,
};

export type RbacAttributeValState = Readonly<typeof initialState>;

// Reducer

export default (state: RbacAttributeValState = initialState, action): RbacAttributeValState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.FETCH_RBACATTRIBUTEVAL_LIST):
    case REQUEST(ACTION_TYPES.FETCH_RBACATTRIBUTEVAL):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true,
      };
    case REQUEST(ACTION_TYPES.CREATE_RBACATTRIBUTEVAL):
    case REQUEST(ACTION_TYPES.UPDATE_RBACATTRIBUTEVAL):
    case REQUEST(ACTION_TYPES.DELETE_RBACATTRIBUTEVAL):
    case REQUEST(ACTION_TYPES.PARTIAL_UPDATE_RBACATTRIBUTEVAL):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true,
      };
    case FAILURE(ACTION_TYPES.FETCH_RBACATTRIBUTEVAL_LIST):
    case FAILURE(ACTION_TYPES.FETCH_RBACATTRIBUTEVAL):
    case FAILURE(ACTION_TYPES.CREATE_RBACATTRIBUTEVAL):
    case FAILURE(ACTION_TYPES.UPDATE_RBACATTRIBUTEVAL):
    case FAILURE(ACTION_TYPES.PARTIAL_UPDATE_RBACATTRIBUTEVAL):
    case FAILURE(ACTION_TYPES.DELETE_RBACATTRIBUTEVAL):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload,
      };
    case SUCCESS(ACTION_TYPES.FETCH_RBACATTRIBUTEVAL_LIST):
      return {
        ...state,
        loading: false,
        entities: action.payload.data,
        totalItems: parseInt(action.payload.headers['x-total-count'], 10),
      };
    case SUCCESS(ACTION_TYPES.FETCH_RBACATTRIBUTEVAL):
      return {
        ...state,
        loading: false,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.CREATE_RBACATTRIBUTEVAL):
    case SUCCESS(ACTION_TYPES.UPDATE_RBACATTRIBUTEVAL):
    case SUCCESS(ACTION_TYPES.PARTIAL_UPDATE_RBACATTRIBUTEVAL):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.DELETE_RBACATTRIBUTEVAL):
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

const apiUrl = 'services/dataacm/api/rbac-attribute-vals';

// Actions

export const getEntities: ICrudGetAllAction<IRbacAttributeVal> = (page, size, sort) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
  return {
    type: ACTION_TYPES.FETCH_RBACATTRIBUTEVAL_LIST,
    payload: axios.get<IRbacAttributeVal>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`),
  };
};

export const getEntity: ICrudGetAction<IRbacAttributeVal> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_RBACATTRIBUTEVAL,
    payload: axios.get<IRbacAttributeVal>(requestUrl),
  };
};

export const createEntity: ICrudPutAction<IRbacAttributeVal> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_RBACATTRIBUTEVAL,
    payload: axios.post(apiUrl, cleanEntity(entity)),
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<IRbacAttributeVal> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_RBACATTRIBUTEVAL,
    payload: axios.put(`${apiUrl}/${entity.id}`, cleanEntity(entity)),
  });
  return result;
};

export const partialUpdate: ICrudPutAction<IRbacAttributeVal> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.PARTIAL_UPDATE_RBACATTRIBUTEVAL,
    payload: axios.patch(`${apiUrl}/${entity.id}`, cleanEntity(entity)),
  });
  return result;
};

export const deleteEntity: ICrudDeleteAction<IRbacAttributeVal> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_RBACATTRIBUTEVAL,
    payload: axios.delete(requestUrl),
  });
  dispatch(getEntities());
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET,
});
