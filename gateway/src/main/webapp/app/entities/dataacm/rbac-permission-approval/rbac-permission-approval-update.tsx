import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvFeedback, AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
import { Translate, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { IRbacPermission } from 'app/shared/model/dataacm/rbac-permission.model';
import { getEntities as getRbacPermissions } from 'app/entities/dataacm/rbac-permission/rbac-permission.reducer';
import { getEntity, updateEntity, createEntity, reset } from './rbac-permission-approval.reducer';
import { IRbacPermissionApproval } from 'app/shared/model/dataacm/rbac-permission-approval.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IRbacPermissionApprovalUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const RbacPermissionApprovalUpdate = (props: IRbacPermissionApprovalUpdateProps) => {
  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const { rbacPermissionApprovalEntity, rbacPermissions, loading, updating } = props;

  const handleClose = () => {
    props.history.push('/rbac-permission-approval' + props.location.search);
  };

  useEffect(() => {
    if (isNew) {
      props.reset();
    } else {
      props.getEntity(props.match.params.id);
    }

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
        ...rbacPermissionApprovalEntity,
        ...values,
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
          <h2 id="gatewayApp.dataacmRbacPermissionApproval.home.createOrEditLabel" data-cy="RbacPermissionApprovalCreateUpdateHeading">
            <Translate contentKey="gatewayApp.dataacmRbacPermissionApproval.home.createOrEditLabel">
              Create or edit a RbacPermissionApproval
            </Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <AvForm model={isNew ? {} : rbacPermissionApprovalEntity} onSubmit={saveEntity}>
              {!isNew ? (
                <AvGroup>
                  <Label for="rbac-permission-approval-id">
                    <Translate contentKey="gatewayApp.dataacmRbacPermissionApproval.id">Id</Translate>
                  </Label>
                  <AvInput id="rbac-permission-approval-id" type="text" className="form-control" name="id" required readOnly />
                </AvGroup>
              ) : null}
              <AvGroup>
                <Label id="descLabel" for="rbac-permission-approval-desc">
                  <Translate contentKey="gatewayApp.dataacmRbacPermissionApproval.desc">Desc</Translate>
                </Label>
                <AvField
                  id="rbac-permission-approval-desc"
                  data-cy="desc"
                  type="text"
                  name="desc"
                  validate={{
                    required: { value: true, errorMessage: translate('entity.validation.required') },
                  }}
                />
              </AvGroup>
              <AvGroup>
                <Label id="approverEmailLabel" for="rbac-permission-approval-approverEmail">
                  <Translate contentKey="gatewayApp.dataacmRbacPermissionApproval.approverEmail">Approver Email</Translate>
                </Label>
                <AvField
                  id="rbac-permission-approval-approverEmail"
                  data-cy="approverEmail"
                  type="text"
                  name="approverEmail"
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
                <Label id="statusLabel" for="rbac-permission-approval-status">
                  <Translate contentKey="gatewayApp.dataacmRbacPermissionApproval.status">Status</Translate>
                </Label>
                <AvInput
                  id="rbac-permission-approval-status"
                  data-cy="status"
                  type="select"
                  className="form-control"
                  name="status"
                  value={(!isNew && rbacPermissionApprovalEntity.status) || 'APPROVED'}
                >
                  <option value="APPROVED">{translate('gatewayApp.RbacPermissionApprovalStatus.APPROVED')}</option>
                  <option value="PENDING">{translate('gatewayApp.RbacPermissionApprovalStatus.PENDING')}</option>
                  <option value="REJECTED">{translate('gatewayApp.RbacPermissionApprovalStatus.REJECTED')}</option>
                </AvInput>
              </AvGroup>
              <Button tag={Link} id="cancel-save" to="/rbac-permission-approval" replace color="info">
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
  rbacPermissions: storeState.rbacPermission.entities,
  rbacPermissionApprovalEntity: storeState.rbacPermissionApproval.entity,
  loading: storeState.rbacPermissionApproval.loading,
  updating: storeState.rbacPermissionApproval.updating,
  updateSuccess: storeState.rbacPermissionApproval.updateSuccess,
});

const mapDispatchToProps = {
  getRbacPermissions,
  getEntity,
  updateEntity,
  createEntity,
  reset,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(RbacPermissionApprovalUpdate);
