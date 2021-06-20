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
import { getEntity, updateEntity, createEntity, reset } from './rbac-data-access.reducer';
import { IRbacDataAccess } from 'app/shared/model/dataacm/rbac-data-access.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IRbacDataAccessUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const RbacDataAccessUpdate = (props: IRbacDataAccessUpdateProps) => {
  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const { rbacDataAccessEntity, rbacSecurityGroups, loading, updating } = props;

  const handleClose = () => {
    props.history.push('/rbac-data-access' + props.location.search);
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
        ...rbacDataAccessEntity,
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
          <h2 id="gatewayApp.dataacmRbacDataAccess.home.createOrEditLabel" data-cy="RbacDataAccessCreateUpdateHeading">
            <Translate contentKey="gatewayApp.dataacmRbacDataAccess.home.createOrEditLabel">Create or edit a RbacDataAccess</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <AvForm model={isNew ? {} : rbacDataAccessEntity} onSubmit={saveEntity}>
              {!isNew ? (
                <AvGroup>
                  <Label for="rbac-data-access-id">
                    <Translate contentKey="gatewayApp.dataacmRbacDataAccess.id">Id</Translate>
                  </Label>
                  <AvInput id="rbac-data-access-id" type="text" className="form-control" name="id" required readOnly />
                </AvGroup>
              ) : null}
              <AvGroup>
                <Label id="nameLabel" for="rbac-data-access-name">
                  <Translate contentKey="gatewayApp.dataacmRbacDataAccess.name">Name</Translate>
                </Label>
                <AvField
                  id="rbac-data-access-name"
                  data-cy="name"
                  type="text"
                  name="name"
                  validate={{
                    required: { value: true, errorMessage: translate('entity.validation.required') },
                  }}
                />
              </AvGroup>
              <AvGroup>
                <Label id="descLabel" for="rbac-data-access-desc">
                  <Translate contentKey="gatewayApp.dataacmRbacDataAccess.desc">Desc</Translate>
                </Label>
                <AvField
                  id="rbac-data-access-desc"
                  data-cy="desc"
                  type="text"
                  name="desc"
                  validate={{
                    required: { value: true, errorMessage: translate('entity.validation.required') },
                  }}
                />
              </AvGroup>
              <Button tag={Link} id="cancel-save" to="/rbac-data-access" replace color="info">
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
  rbacDataAccessEntity: storeState.rbacDataAccess.entity,
  loading: storeState.rbacDataAccess.loading,
  updating: storeState.rbacDataAccess.updating,
  updateSuccess: storeState.rbacDataAccess.updateSuccess,
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

export default connect(mapStateToProps, mapDispatchToProps)(RbacDataAccessUpdate);
