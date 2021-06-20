import axios from 'axios';
import { ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';
import { IRbacDataAccess, defaultValue } from 'app/shared/model/dataacm/rbac-data-access.model';

export const ACTION_TYPES = {
  FETCH_RBACDATAACCESS_LIST: 'rbacDataAccess/FETCH_RBACDATAACCESS_LIST',
  FETCH_RBACDATAACCESS: 'rbacDataAccess/FETCH_RBACDATAACCESS',
  CREATE_RBACDATAACCESS: 'rbacDataAccess/CREATE_RBACDATAACCESS',
  UPDATE_RBACDATAACCESS: 'rbacDataAccess/UPDATE_RBACDATAACCESS',
  PARTIAL_UPDATE_RBACDATAACCESS: 'rbacDataAccess/PARTIAL_UPDATE_RBACDATAACCESS',
  DELETE_RBACDATAACCESS: 'rbacDataAccess/DELETE_RBACDATAACCESS',
  RESET: 'rbacDataAccess/RESET',
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IRbacDataAccess>,
  entity: defaultValue,
  updating: false,
  totalItems: 0,
  updateSuccess: false,
};

export type RbacDataAccessState = Readonly<typeof initialState>;

// Reducer

export default (state: RbacDataAccessState = initialState, action): RbacDataAccessState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.FETCH_RBACDATAACCESS_LIST):
    case REQUEST(ACTION_TYPES.FETCH_RBACDATAACCESS):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true,
      };
    case REQUEST(ACTION_TYPES.CREATE_RBACDATAACCESS):
    case REQUEST(ACTION_TYPES.UPDATE_RBACDATAACCESS):
    case REQUEST(ACTION_TYPES.DELETE_RBACDATAACCESS):
    case REQUEST(ACTION_TYPES.PARTIAL_UPDATE_RBACDATAACCESS):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true,
      };
    case FAILURE(ACTION_TYPES.FETCH_RBACDATAACCESS_LIST):
    case FAILURE(ACTION_TYPES.FETCH_RBACDATAACCESS):
    case FAILURE(ACTION_TYPES.CREATE_RBACDATAACCESS):
    case FAILURE(ACTION_TYPES.UPDATE_RBACDATAACCESS):
    case FAILURE(ACTION_TYPES.PARTIAL_UPDATE_RBACDATAACCESS):
    case FAILURE(ACTION_TYPES.DELETE_RBACDATAACCESS):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload,
      };
    case SUCCESS(ACTION_TYPES.FETCH_RBACDATAACCESS_LIST):
      return {
        ...state,
        loading: false,
        entities: action.payload.data,
        totalItems: parseInt(action.payload.headers['x-total-count'], 10),
      };
    case SUCCESS(ACTION_TYPES.FETCH_RBACDATAACCESS):
      return {
        ...state,
        loading: false,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.CREATE_RBACDATAACCESS):
    case SUCCESS(ACTION_TYPES.UPDATE_RBACDATAACCESS):
    case SUCCESS(ACTION_TYPES.PARTIAL_UPDATE_RBACDATAACCESS):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.DELETE_RBACDATAACCESS):
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

const apiUrl = 'services/dataacm/api/rbac-data-accesses';

// Actions

export const getEntities: ICrudGetAllAction<IRbacDataAccess> = (page, size, sort) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
  return {
    type: ACTION_TYPES.FETCH_RBACDATAACCESS_LIST,
    payload: axios.get<IRbacDataAccess>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`),
  };
};

export const getEntity: ICrudGetAction<IRbacDataAccess> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_RBACDATAACCESS,
    payload: axios.get<IRbacDataAccess>(requestUrl),
  };
};

export const createEntity: ICrudPutAction<IRbacDataAccess> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_RBACDATAACCESS,
    payload: axios.post(apiUrl, cleanEntity(entity)),
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<IRbacDataAccess> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_RBACDATAACCESS,
    payload: axios.put(`${apiUrl}/${entity.id}`, cleanEntity(entity)),
  });
  return result;
};

export const partialUpdate: ICrudPutAction<IRbacDataAccess> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.PARTIAL_UPDATE_RBACDATAACCESS,
    payload: axios.patch(`${apiUrl}/${entity.id}`, cleanEntity(entity)),
  });
  return result;
};

export const deleteEntity: ICrudDeleteAction<IRbacDataAccess> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_RBACDATAACCESS,
    payload: axios.delete(requestUrl),
  });
  dispatch(getEntities());
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET,
});
