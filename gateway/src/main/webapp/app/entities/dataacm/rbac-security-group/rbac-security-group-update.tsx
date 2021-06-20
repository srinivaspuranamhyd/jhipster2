import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvFeedback, AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
import { Translate, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { IRbacDataAccess } from 'app/shared/model/dataacm/rbac-data-access.model';
import { getEntities as getRbacDataAccesses } from 'app/entities/dataacm/rbac-data-access/rbac-data-access.reducer';
import { IRbacUser } from 'app/shared/model/dataacm/rbac-user.model';
import { getEntities as getRbacUsers } from 'app/entities/dataacm/rbac-user/rbac-user.reducer';
import { getEntity, updateEntity, createEntity, reset } from './rbac-security-group.reducer';
import { IRbacSecurityGroup } from 'app/shared/model/dataacm/rbac-security-group.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IRbacSecurityGroupUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const RbacSecurityGroupUpdate = (props: IRbacSecurityGroupUpdateProps) => {
  const [idsdataTopic, setIdsdataTopic] = useState([]);
  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const { rbacSecurityGroupEntity, rbacDataAccesses, rbacUsers, loading, updating } = props;

  const handleClose = () => {
    props.history.push('/rbac-security-group' + props.location.search);
  };

  useEffect(() => {
    if (isNew) {
      props.reset();
    } else {
      props.getEntity(props.match.params.id);
    }

    props.getRbacDataAccesses();
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
        ...rbacSecurityGroupEntity,
        ...values,
        dataTopics: mapIdList(values.dataTopics),
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
          <h2 id="gatewayApp.dataacmRbacSecurityGroup.home.createOrEditLabel" data-cy="RbacSecurityGroupCreateUpdateHeading">
            <Translate contentKey="gatewayApp.dataacmRbacSecurityGroup.home.createOrEditLabel">
              Create or edit a RbacSecurityGroup
            </Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <AvForm model={isNew ? {} : rbacSecurityGroupEntity} onSubmit={saveEntity}>
              {!isNew ? (
                <AvGroup>
                  <Label for="rbac-security-group-id">
                    <Translate contentKey="global.field.id">ID</Translate>
                  </Label>
                  <AvInput id="rbac-security-group-id" type="text" className="form-control" name="id" required readOnly />
                </AvGroup>
              ) : null}
              <AvGroup>
                <Label id="nameLabel" for="rbac-security-group-name">
                  <Translate contentKey="gatewayApp.dataacmRbacSecurityGroup.name">Name</Translate>
                </Label>
                <AvField
                  id="rbac-security-group-name"
                  data-cy="name"
                  type="text"
                  name="name"
                  validate={{
                    required: { value: true, errorMessage: translate('entity.validation.required') },
                  }}
                />
              </AvGroup>
              <AvGroup>
                <Label id="descLabel" for="rbac-security-group-desc">
                  <Translate contentKey="gatewayApp.dataacmRbacSecurityGroup.desc">Desc</Translate>
                </Label>
                <AvField
                  id="rbac-security-group-desc"
                  data-cy="desc"
                  type="text"
                  name="desc"
                  validate={{
                    required: { value: true, errorMessage: translate('entity.validation.required') },
                  }}
                />
              </AvGroup>
              <AvGroup>
                <Label for="rbac-security-group-dataTopic">
                  <Translate contentKey="gatewayApp.dataacmRbacSecurityGroup.dataTopic">Data Topic</Translate>
                </Label>
                <AvInput
                  id="rbac-security-group-dataTopic"
                  data-cy="dataTopic"
                  type="select"
                  multiple
                  className="form-control"
                  name="dataTopics"
                  value={!isNew && rbacSecurityGroupEntity.dataTopics && rbacSecurityGroupEntity.dataTopics.map(e => e.id)}
                >
                  <option value="" key="0" />
                  {rbacDataAccesses
                    ? rbacDataAccesses.map(otherEntity => (
                        <option value={otherEntity.id} key={otherEntity.id}>
                          {otherEntity.id}
                        </option>
                      ))
                    : null}
                </AvInput>
              </AvGroup>
              <Button tag={Link} id="cancel-save" to="/rbac-security-group" replace color="info">
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
  rbacDataAccesses: storeState.rbacDataAccess.entities,
  rbacUsers: storeState.rbacUser.entities,
  rbacSecurityGroupEntity: storeState.rbacSecurityGroup.entity,
  loading: storeState.rbacSecurityGroup.loading,
  updating: storeState.rbacSecurityGroup.updating,
  updateSuccess: storeState.rbacSecurityGroup.updateSuccess,
});

const mapDispatchToProps = {
  getRbacDataAccesses,
  getRbacUsers,
  getEntity,
  updateEntity,
  createEntity,
  reset,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(RbacSecurityGroupUpdate);
