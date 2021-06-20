import axios from 'axios';
import { ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';
import { IRbacUser, defaultValue } from 'app/shared/model/dataacm/rbac-user.model';

export const ACTION_TYPES = {
  FETCH_RBACUSER_LIST: 'rbacUser/FETCH_RBACUSER_LIST',
  FETCH_RBACUSER: 'rbacUser/FETCH_RBACUSER',
  CREATE_RBACUSER: 'rbacUser/CREATE_RBACUSER',
  UPDATE_RBACUSER: 'rbacUser/UPDATE_RBACUSER',
  PARTIAL_UPDATE_RBACUSER: 'rbacUser/PARTIAL_UPDATE_RBACUSER',
  DELETE_RBACUSER: 'rbacUser/DELETE_RBACUSER',
  RESET: 'rbacUser/RESET',
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IRbacUser>,
  entity: defaultValue,
  updating: false,
  totalItems: 0,
  updateSuccess: false,
};

export type RbacUserState = Readonly<typeof initialState>;

// Reducer

export default (state: RbacUserState = initialState, action): RbacUserState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.FETCH_RBACUSER_LIST):
    case REQUEST(ACTION_TYPES.FETCH_RBACUSER):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true,
      };
    case REQUEST(ACTION_TYPES.CREATE_RBACUSER):
    case REQUEST(ACTION_TYPES.UPDATE_RBACUSER):
    case REQUEST(ACTION_TYPES.DELETE_RBACUSER):
    case REQUEST(ACTION_TYPES.PARTIAL_UPDATE_RBACUSER):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true,
      };
    case FAILURE(ACTION_TYPES.FETCH_RBACUSER_LIST):
    case FAILURE(ACTION_TYPES.FETCH_RBACUSER):
    case FAILURE(ACTION_TYPES.CREATE_RBACUSER):
    case FAILURE(ACTION_TYPES.UPDATE_RBACUSER):
    case FAILURE(ACTION_TYPES.PARTIAL_UPDATE_RBACUSER):
    case FAILURE(ACTION_TYPES.DELETE_RBACUSER):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload,
      };
    case SUCCESS(ACTION_TYPES.FETCH_RBACUSER_LIST):
      return {
        ...state,
        loading: false,
        entities: action.payload.data,
        totalItems: parseInt(action.payload.headers['x-total-count'], 10),
      };
    case SUCCESS(ACTION_TYPES.FETCH_RBACUSER):
      return {
        ...state,
        loading: false,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.CREATE_RBACUSER):
    case SUCCESS(ACTION_TYPES.UPDATE_RBACUSER):
    case SUCCESS(ACTION_TYPES.PARTIAL_UPDATE_RBACUSER):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.DELETE_RBACUSER):
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

const apiUrl = 'services/dataacm/api/rbac-users';

// Actions

export const getEntities: ICrudGetAllAction<IRbacUser> = (page, size, sort) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
  return {
    type: ACTION_TYPES.FETCH_RBACUSER_LIST,
    payload: axios.get<IRbacUser>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`),
  };
};

export const getEntity: ICrudGetAction<IRbacUser> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_RBACUSER,
    payload: axios.get<IRbacUser>(requestUrl),
  };
};

export const createEntity: ICrudPutAction<IRbacUser> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_RBACUSER,
    payload: axios.post(apiUrl, cleanEntity(entity)),
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<IRbacUser> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_RBACUSER,
    payload: axios.put(`${apiUrl}/${entity.id}`, cleanEntity(entity)),
  });
  return result;
};

export const partialUpdate: ICrudPutAction<IRbacUser> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.PARTIAL_UPDATE_RBACUSER,
    payload: axios.patch(`${apiUrl}/${entity.id}`, cleanEntity(entity)),
  });
  return result;
};

export const deleteEntity: ICrudDeleteAction<IRbacUser> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_RBACUSER,
    payload: axios.delete(requestUrl),
  });
  dispatch(getEntities());
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET,
});
