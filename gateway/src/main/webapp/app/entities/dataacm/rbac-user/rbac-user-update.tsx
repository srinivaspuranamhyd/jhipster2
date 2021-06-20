import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvFeedback, AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
import { Translate, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { IRbacSecurityGroup } from 'app/shared/model/dataacm/rbac-security-group.model';
import { getEntities as getRbacSecurityGroups } from 'app/entities/dataacm/rbac-security-group/rbac-security-group.reducer';
import { getEntity, updateEntity, createEntity, reset } from './rbac-user.reducer';
import { IRbacUser } from 'app/shared/model/dataacm/rbac-user.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IRbacUserUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const RbacUserUpdate = (props: IRbacUserUpdateProps) => {
  const [idsgroup, setIdsgroup] = useState([]);
  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const { rbacUserEntity, rbacSecurityGroups, loading, updating } = props;

  const handleClose = () => {
    props.history.push('/rbac-user' + props.location.search);
  };

  useEffect(() => {
    if (isNew) {
      props.reset();
    } else {
      props.getEntity(props.match.params.id);
    }

    props.getRbacSecurityGroups();
  }, []);

  useEffect(() => {
    if (props.updateSuccess) {
      handleClose();
    }
  }, [props.updateSuccess]);

  const saveEntity = (event, errors, values) => {
    if (errors.length === 0) {
      const entity = {
        ...rbacUserEntity,
        ...values,
        groups: mapIdList(values.groups),
      };

      if (isNew) {
        props.createEntity(entity);
      } else {
        props.updateEntity(entity);
      }
    }
  };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="gatewayApp.dataacmRbacUser.home.createOrEditLabel" data-cy="RbacUserCreateUpdateHeading">
            <Translate contentKey="gatewayApp.dataacmRbacUser.home.createOrEditLabel">Create or edit a RbacUser</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <AvForm model={isNew ? {} : rbacUserEntity} onSubmit={saveEntity}>
              {!isNew ? (
                <AvGroup>
                  <Label for="rbac-user-id">
                    <Translate contentKey="global.field.id">ID</Translate>
                  </Label>
                  <AvInput id="rbac-user-id" type="text" className="form-control" name="id" required readOnly />
                </AvGroup>
              ) : null}
              <AvGroup>
                <Label id="onebankIdLabel" for="rbac-user-onebankId">
                  <Translate contentKey="gatewayApp.dataacmRbacUser.onebankId">Onebank Id</Translate>
                </Label>
                <AvField
                  id="rbac-user-onebankId"
                  data-cy="onebankId"
                  type="text"
                  name="onebankId"
                  validate={{
                    required: { value: true, errorMessage: translate('entity.validation.required') },
                  }}
                />
              </AvGroup>
              <AvGroup>
                <Label id="lanIdLabel" for="rbac-user-lanId">
                  <Translate contentKey="gatewayApp.dataacmRbacUser.lanId">Lan Id</Translate>
                </Label>
                <AvField
                  id="rbac-user-lanId"
                  data-cy="lanId"
                  type="text"
                  name="lanId"
                  validate={{
                    required: { value: true, errorMessage: translate('entity.validation.required') },
                  }}
                />
              </AvGroup>
              <AvGroup>
                <Label id="emailLabel" for="rbac-user-email">
                  <Translate contentKey="gatewayApp.dataacmRbacUser.email">Email</Translate>
                </Label>
                <AvField
                  id="rbac-user-email"
                  data-cy="email"
                  type="text"
                  name="email"
                  validate={{
                    required: { value: true, errorMessage: translate('entity.validation.required') },
                    pattern: {
                      value: '^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$',
                      errorMessage: translate('entity.validation.pattern', { pattern: '^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$' }),
                    },
                  }}
                />
              </AvGroup>
              <AvGroup>
                <Label id="statusLabel" for="rbac-user-status">
                  <Translate contentKey="gatewayApp.dataacmRbacUser.status">Status</Translate>
                </Label>
                <AvInput
                  id="rbac-user-status"
                  data-cy="status"
                  type="select"
                  className="form-control"
                  name="status"
                  value={(!isNew && rbacUserEntity.status) || 'ACTIVE'}
                >
                  <option value="ACTIVE">{translate('gatewayApp.RbacUserStatus.ACTIVE')}</option>
                  <option value="INACTIVE">{translate('gatewayApp.RbacUserStatus.INACTIVE')}</option>
                  <option value="LOCKED">{translate('gatewayApp.RbacUserStatus.LOCKED')}</option>
                </AvInput>
              </AvGroup>
              <AvGroup>
                <Label id="departmentLabel" for="rbac-user-department">
                  <Translate contentKey="gatewayApp.dataacmRbacUser.department">Department</Translate>
                </Label>
                <AvField
                  id="rbac-user-department"
                  data-cy="department"
                  type="text"
                  name="department"
                  validate={{
                    required: { value: true, errorMessage: translate('entity.validation.required') },
                  }}
                />
              </AvGroup>
              <AvGroup>
                <Label id="countryLabel" for="rbac-user-country">
                  <Translate contentKey="gatewayApp.dataacmRbacUser.country">Country</Translate>
                </Label>
                <AvField
                  id="rbac-user-country"
                  data-cy="country"
                  type="text"
                  name="country"
                  validate={{
                    required: { value: true, errorMessage: translate('entity.validation.required') },
                  }}
                />
              </AvGroup>
              <AvGroup>
                <Label id="managerIdLabel" for="rbac-user-managerId">
                  <Translate contentKey="gatewayApp.dataacmRbacUser.managerId">Manager Id</Translate>
                </Label>
                <AvField id="rbac-user-managerId" data-cy="managerId" type="text" name="managerId" />
              </AvGroup>
              <AvGroup>
                <Label for="rbac-user-group">
                  <Translate contentKey="gatewayApp.dataacmRbacUser.group">Group</Translate>
                </Label>
                <AvInput
                  id="rbac-user-group"
                  data-cy="group"
                  type="select"
                  multiple
                  className="form-control"
                  name="groups"
                  value={!isNew && rbacUserEntity.groups && rbacUserEntity.groups.map(e => e.id)}
                >
                  <option value="" key="0" />
                  {rbacSecurityGroups
                    ? rbacSecurityGroups.map(otherEntity => (
                        <option value={otherEntity.id} key={otherEntity.id}>
                          {otherEntity.id}
                        </option>
                      ))
                    : null}
                </AvInput>
              </AvGroup>
              <Button tag={Link} id="cancel-save" to="/rbac-user" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </AvForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

const mapStateToProps = (storeState: IRootState) => ({
  rbacSecurityGroups: storeState.rbacSecurityGroup.entities,
  rbacUserEntity: storeState.rbacUser.entity,
  loading: storeState.rbacUser.loading,
  updating: storeState.rbacUser.updating,
  updateSuccess: storeState.rbacUser.updateSuccess,
});

const mapDispatchToProps = {
  getRbacSecurityGroups,
  getEntity,
  updateEntity,
  createEntity,
  reset,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(RbacUserUpdate);
