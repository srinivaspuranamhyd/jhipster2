import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvFeedback, AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
import { Translate, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { IRbacPolicy } from 'app/shared/model/dataacm/rbac-policy.model';
import { getEntities as getRbacPolicies } from 'app/entities/dataacm/rbac-policy/rbac-policy.reducer';
import { IRbacPermission } from 'app/shared/model/dataacm/rbac-permission.model';
import { getEntities as getRbacPermissions } from 'app/entities/dataacm/rbac-permission/rbac-permission.reducer';
import { getEntity, updateEntity, createEntity, reset } from './rbac-attribute.reducer';
import { IRbacAttribute } from 'app/shared/model/dataacm/rbac-attribute.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IRbacAttributeUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const RbacAttributeUpdate = (props: IRbacAttributeUpdateProps) => {
  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const { rbacAttributeEntity, rbacPolicies, rbacPermissions, loading, updating } = props;

  const handleClose = () => {
    props.history.push('/rbac-attribute' + props.location.search);
  };

  useEffect(() => {
    if (isNew) {
      props.reset();
    } else {
      props.getEntity(props.match.params.id);
    }

    props.getRbacPolicies();
    props.getRbacPermissions();
  }, []);

  useEffect(() => {
    if (props.updateSuccess) {
      handleClose();
    }
  }, [props.updateSuccess]);

  const saveEntity = (event, errors, values) => {
    if (errors.length === 0) {
      const entity = {
        ...rbacAttributeEntity,
        ...values,
        rbacPolicy: rbacPolicies.find(it => it.id.toString() === values.rbacPolicyId.toString()),
        rbacPermission: rbacPermissions.find(it => it.id.toString() === values.rbacPermissionId.toString()),
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
          <h2 id="gatewayApp.dataacmRbacAttribute.home.createOrEditLabel" data-cy="RbacAttributeCreateUpdateHeading">
            <Translate contentKey="gatewayApp.dataacmRbacAttribute.home.createOrEditLabel">Create or edit a RbacAttribute</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <AvForm model={isNew ? {} : rbacAttributeEntity} onSubmit={saveEntity}>
              {!isNew ? (
                <AvGroup>
                  <Label for="rbac-attribute-id">
                    <Translate contentKey="gatewayApp.dataacmRbacAttribute.id">Id</Translate>
                  </Label>
                  <AvInput id="rbac-attribute-id" type="text" className="form-control" name="id" required readOnly />
                </AvGroup>
              ) : null}
              <AvGroup>
                <Label id="nameLabel" for="rbac-attribute-name">
                  <Translate contentKey="gatewayApp.dataacmRbacAttribute.name">Name</Translate>
                </Label>
                <AvField
                  id="rbac-attribute-name"
                  data-cy="name"
                  type="text"
                  name="name"
                  validate={{
                    required: { value: true, errorMessage: translate('entity.validation.required') },
                  }}
                />
              </AvGroup>
              <AvGroup>
                <Label id="typeLabel" for="rbac-attribute-type">
                  <Translate contentKey="gatewayApp.dataacmRbacAttribute.type">Type</Translate>
                </Label>
                <AvField
                  id="rbac-attribute-type"
                  data-cy="type"
                  type="text"
                  name="type"
                  validate={{
                    required: { value: true, errorMessage: translate('entity.validation.required') },
                  }}
                />
              </AvGroup>
              <AvGroup>
                <Label for="rbac-attribute-rbacPolicy">
                  <Translate contentKey="gatewayApp.dataacmRbacAttribute.rbacPolicy">Rbac Policy</Translate>
                </Label>
                <AvInput id="rbac-attribute-rbacPolicy" data-cy="rbacPolicy" type="select" className="form-control" name="rbacPolicyId">
                  <option value="" key="0" />
                  {rbacPolicies
                    ? rbacPolicies.map(otherEntity => (
                        <option value={otherEntity.id} key={otherEntity.id}>
                          {otherEntity.id}
                        </option>
                      ))
                    : null}
                </AvInput>
              </AvGroup>
              <AvGroup>
                <Label for="rbac-attribute-rbacPermission">
                  <Translate contentKey="gatewayApp.dataacmRbacAttribute.rbacPermission">Rbac Permission</Translate>
                </Label>
                <AvInput
                  id="rbac-attribute-rbacPermission"
                  data-cy="rbacPermission"
                  type="select"
                  className="form-control"
                  name="rbacPermissionId"
                >
                  <option value="" key="0" />
                  {rbacPermissions
                    ? rbacPermissions.map(otherEntity => (
                        <option value={otherEntity.id} key={otherEntity.id}>
                          {otherEntity.id}
                        </option>
                      ))
                    : null}
                </AvInput>
              </AvGroup>
              <Button tag={Link} id="cancel-save" to="/rbac-attribute" replace color="info">
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
  rbacPolicies: storeState.rbacPolicy.entities,
  rbacPermissions: storeState.rbacPermission.entities,
  rbacAttributeEntity: storeState.rbacAttribute.entity,
  loading: storeState.rbacAttribute.loading,
  updating: storeState.rbacAttribute.updating,
  updateSuccess: storeState.rbacAttribute.updateSuccess,
});

const mapDispatchToProps = {
  getRbacPolicies,
  getRbacPermissions,
  getEntity,
  updateEntity,
  createEntity,
  reset,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(RbacAttributeUpdate);
