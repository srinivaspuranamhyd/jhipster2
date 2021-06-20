import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvFeedback, AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
import { Translate, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { IRbacUser } from 'app/shared/model/dataacm/rbac-user.model';
import { getEntities as getRbacUsers } from 'app/entities/dataacm/rbac-user/rbac-user.reducer';
import { getEntity, updateEntity, createEntity, reset } from './rbac-policy.reducer';
import { IRbacPolicy } from 'app/shared/model/dataacm/rbac-policy.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IRbacPolicyUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const RbacPolicyUpdate = (props: IRbacPolicyUpdateProps) => {
  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const { rbacPolicyEntity, rbacUsers, loading, updating } = props;

  const handleClose = () => {
    props.history.push('/rbac-policy' + props.location.search);
  };

  useEffect(() => {
    if (isNew) {
      props.reset();
    } else {
      props.getEntity(props.match.params.id);
    }

    props.getRbacUsers();
  }, []);

  useEffect(() => {
    if (props.updateSuccess) {
      handleClose();
    }
  }, [props.updateSuccess]);

  const saveEntity = (event, errors, values) => {
    if (errors.length === 0) {
      const entity = {
        ...rbacPolicyEntity,
        ...values,
        user: rbacUsers.find(it => it.id.toString() === values.userId.toString()),
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
          <h2 id="gatewayApp.dataacmRbacPolicy.home.createOrEditLabel" data-cy="RbacPolicyCreateUpdateHeading">
            <Translate contentKey="gatewayApp.dataacmRbacPolicy.home.createOrEditLabel">Create or edit a RbacPolicy</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <AvForm model={isNew ? {} : rbacPolicyEntity} onSubmit={saveEntity}>
              {!isNew ? (
                <AvGroup>
                  <Label for="rbac-policy-id">
                    <Translate contentKey="gatewayApp.dataacmRbacPolicy.id">Id</Translate>
                  </Label>
                  <AvInput id="rbac-policy-id" type="text" className="form-control" name="id" required readOnly />
                </AvGroup>
              ) : null}
              <AvGroup>
                <Label id="nameLabel" for="rbac-policy-name">
                  <Translate contentKey="gatewayApp.dataacmRbacPolicy.name">Name</Translate>
                </Label>
                <AvField
                  id="rbac-policy-name"
                  data-cy="name"
                  type="text"
                  name="name"
                  validate={{
                    required: { value: true, errorMessage: translate('entity.validation.required') },
                  }}
                />
              </AvGroup>
              <AvGroup>
                <Label id="descLabel" for="rbac-policy-desc">
                  <Translate contentKey="gatewayApp.dataacmRbacPolicy.desc">Desc</Translate>
                </Label>
                <AvField
                  id="rbac-policy-desc"
                  data-cy="desc"
                  type="text"
                  name="desc"
                  validate={{
                    required: { value: true, errorMessage: translate('entity.validation.required') },
                  }}
                />
              </AvGroup>
              <AvGroup>
                <Label for="rbac-policy-user">
                  <Translate contentKey="gatewayApp.dataacmRbacPolicy.user">User</Translate>
                </Label>
                <AvInput id="rbac-policy-user" data-cy="user" type="select" className="form-control" name="userId">
                  <option value="" key="0" />
                  {rbacUsers
                    ? rbacUsers.map(otherEntity => (
                        <option value={otherEntity.id} key={otherEntity.id}>
                          {otherEntity.id}
                        </option>
                      ))
                    : null}
                </AvInput>
              </AvGroup>
              <Button tag={Link} id="cancel-save" to="/rbac-policy" replace color="info">
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
  rbacUsers: storeState.rbacUser.entities,
  rbacPolicyEntity: storeState.rbacPolicy.entity,
  loading: storeState.rbacPolicy.loading,
  updating: storeState.rbacPolicy.updating,
  updateSuccess: storeState.rbacPolicy.updateSuccess,
});

const mapDispatchToProps = {
  getRbacUsers,
  getEntity,
  updateEntity,
  createEntity,
  reset,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(RbacPolicyUpdate);
