import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvFeedback, AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
import { Translate, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { IRbacPermissionApproval } from 'app/shared/model/dataacm/rbac-permission-approval.model';
import { getEntities as getRbacPermissionApprovals } from 'app/entities/dataacm/rbac-permission-approval/rbac-permission-approval.reducer';
import { IRbacUser } from 'app/shared/model/dataacm/rbac-user.model';
import { getEntities as getRbacUsers } from 'app/entities/dataacm/rbac-user/rbac-user.reducer';
import { getEntity, updateEntity, createEntity, reset } from './rbac-permission.reducer';
import { IRbacPermission } from 'app/shared/model/dataacm/rbac-permission.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IRbacPermissionUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const RbacPermissionUpdate = (props: IRbacPermissionUpdateProps) => {
  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const { rbacPermissionEntity, rbacPermissionApprovals, rbacUsers, loading, updating } = props;

  const handleClose = () => {
    props.history.push('/rbac-permission' + props.location.search);
  };

  useEffect(() => {
    if (isNew) {
      props.reset();
    } else {
      props.getEntity(props.match.params.id);
    }

    props.getRbacPermissionApprovals();
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
        ...rbacPermissionEntity,
        ...values,
        approval: rbacPermissionApprovals.find(it => it.id.toString() === values.approvalId.toString()),
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
          <h2 id="gatewayApp.dataacmRbacPermission.home.createOrEditLabel" data-cy="RbacPermissionCreateUpdateHeading">
            <Translate contentKey="gatewayApp.dataacmRbacPermission.home.createOrEditLabel">Create or edit a RbacPermission</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <AvForm model={isNew ? {} : rbacPermissionEntity} onSubmit={saveEntity}>
              {!isNew ? (
                <AvGroup>
                  <Label for="rbac-permission-id">
                    <Translate contentKey="gatewayApp.dataacmRbacPermission.id">Id</Translate>
                  </Label>
                  <AvInput id="rbac-permission-id" type="text" className="form-control" name="id" required readOnly />
                </AvGroup>
              ) : null}
              <AvGroup>
                <Label id="nameLabel" for="rbac-permission-name">
                  <Translate contentKey="gatewayApp.dataacmRbacPermission.name">Name</Translate>
                </Label>
                <AvField
                  id="rbac-permission-name"
                  data-cy="name"
                  type="text"
                  name="name"
                  validate={{
                    required: { value: true, errorMessage: translate('entity.validation.required') },
                  }}
                />
              </AvGroup>
              <AvGroup>
                <Label id="descLabel" for="rbac-permission-desc">
                  <Translate contentKey="gatewayApp.dataacmRbacPermission.desc">Desc</Translate>
                </Label>
                <AvField
                  id="rbac-permission-desc"
                  data-cy="desc"
                  type="text"
                  name="desc"
                  validate={{
                    required: { value: true, errorMessage: translate('entity.validation.required') },
                  }}
                />
              </AvGroup>
              <AvGroup>
                <Label for="rbac-permission-approval">
                  <Translate contentKey="gatewayApp.dataacmRbacPermission.approval">Approval</Translate>
                </Label>
                <AvInput id="rbac-permission-approval" data-cy="approval" type="select" className="form-control" name="approvalId">
                  <option value="" key="0" />
                  {rbacPermissionApprovals
                    ? rbacPermissionApprovals.map(otherEntity => (
                        <option value={otherEntity.id} key={otherEntity.id}>
                          {otherEntity.id}
                        </option>
                      ))
                    : null}
                </AvInput>
              </AvGroup>
              <AvGroup>
                <Label for="rbac-permission-user">
                  <Translate contentKey="gatewayApp.dataacmRbacPermission.user">User</Translate>
                </Label>
                <AvInput id="rbac-permission-user" data-cy="user" type="select" className="form-control" name="userId">
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
              <Button tag={Link} id="cancel-save" to="/rbac-permission" replace color="info">
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
  rbacPermissionApprovals: storeState.rbacPermissionApproval.entities,
  rbacUsers: storeState.rbacUser.entities,
  rbacPermissionEntity: storeState.rbacPermission.entity,
  loading: storeState.rbacPermission.loading,
  updating: storeState.rbacPermission.updating,
  updateSuccess: storeState.rbacPermission.updateSuccess,
});

const mapDispatchToProps = {
  getRbacPermissionApprovals,
  getRbacUsers,
  getEntity,
  updateEntity,
  createEntity,
  reset,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(RbacPermissionUpdate);
