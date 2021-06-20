import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvFeedback, AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
import { Translate, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { IRbacAttribute } from 'app/shared/model/dataacm/rbac-attribute.model';
import { getEntities as getRbacAttributes } from 'app/entities/dataacm/rbac-attribute/rbac-attribute.reducer';
import { getEntity, updateEntity, createEntity, reset } from './rbac-attribute-val.reducer';
import { IRbacAttributeVal } from 'app/shared/model/dataacm/rbac-attribute-val.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IRbacAttributeValUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const RbacAttributeValUpdate = (props: IRbacAttributeValUpdateProps) => {
  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const { rbacAttributeValEntity, rbacAttributes, loading, updating } = props;

  const handleClose = () => {
    props.history.push('/rbac-attribute-val' + props.location.search);
  };

  useEffect(() => {
    if (isNew) {
      props.reset();
    } else {
      props.getEntity(props.match.params.id);
    }

    props.getRbacAttributes();
  }, []);

  useEffect(() => {
    if (props.updateSuccess) {
      handleClose();
    }
  }, [props.updateSuccess]);

  const saveEntity = (event, errors, values) => {
    if (errors.length === 0) {
      const entity = {
        ...rbacAttributeValEntity,
        ...values,
        attr: rbacAttributes.find(it => it.id.toString() === values.attrId.toString()),
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
          <h2 id="gatewayApp.dataacmRbacAttributeVal.home.createOrEditLabel" data-cy="RbacAttributeValCreateUpdateHeading">
            <Translate contentKey="gatewayApp.dataacmRbacAttributeVal.home.createOrEditLabel">Create or edit a RbacAttributeVal</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <AvForm model={isNew ? {} : rbacAttributeValEntity} onSubmit={saveEntity}>
              {!isNew ? (
                <AvGroup>
                  <Label for="rbac-attribute-val-id">
                    <Translate contentKey="gatewayApp.dataacmRbacAttributeVal.id">Id</Translate>
                  </Label>
                  <AvInput id="rbac-attribute-val-id" type="text" className="form-control" name="id" required readOnly />
                </AvGroup>
              ) : null}
              <AvGroup>
                <Label id="valueLabel" for="rbac-attribute-val-value">
                  <Translate contentKey="gatewayApp.dataacmRbacAttributeVal.value">Value</Translate>
                </Label>
                <AvField
                  id="rbac-attribute-val-value"
                  data-cy="value"
                  type="text"
                  name="value"
                  validate={{
                    required: { value: true, errorMessage: translate('entity.validation.required') },
                  }}
                />
              </AvGroup>
              <AvGroup>
                <Label for="rbac-attribute-val-attr">
                  <Translate contentKey="gatewayApp.dataacmRbacAttributeVal.attr">Attr</Translate>
                </Label>
                <AvInput id="rbac-attribute-val-attr" data-cy="attr" type="select" className="form-control" name="attrId">
                  <option value="" key="0" />
                  {rbacAttributes
                    ? rbacAttributes.map(otherEntity => (
                        <option value={otherEntity.id} key={otherEntity.id}>
                          {otherEntity.id}
                        </option>
                      ))
                    : null}
                </AvInput>
              </AvGroup>
              <Button tag={Link} id="cancel-save" to="/rbac-attribute-val" replace color="info">
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
  rbacAttributes: storeState.rbacAttribute.entities,
  rbacAttributeValEntity: storeState.rbacAttributeVal.entity,
  loading: storeState.rbacAttributeVal.loading,
  updating: storeState.rbacAttributeVal.updating,
  updateSuccess: storeState.rbacAttributeVal.updateSuccess,
});

const mapDispatchToProps = {
  getRbacAttributes,
  getEntity,
  updateEntity,
  createEntity,
  reset,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(RbacAttributeValUpdate);
