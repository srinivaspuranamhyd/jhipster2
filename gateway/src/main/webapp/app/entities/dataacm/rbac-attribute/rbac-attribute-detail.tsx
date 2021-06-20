import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './rbac-attribute.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IRbacAttributeDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const RbacAttributeDetail = (props: IRbacAttributeDetailProps) => {
  useEffect(() => {
    props.getEntity(props.match.params.id);
  }, []);

  const { rbacAttributeEntity } = props;
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="rbacAttributeDetailsHeading">
          <Translate contentKey="gatewayApp.dataacmRbacAttribute.detail.title">RbacAttribute</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="gatewayApp.dataacmRbacAttribute.id">Id</Translate>
            </span>
          </dt>
          <dd>{rbacAttributeEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="gatewayApp.dataacmRbacAttribute.name">Name</Translate>
            </span>
          </dt>
          <dd>{rbacAttributeEntity.name}</dd>
          <dt>
            <span id="type">
              <Translate contentKey="gatewayApp.dataacmRbacAttribute.type">Type</Translate>
            </span>
          </dt>
          <dd>{rbacAttributeEntity.type}</dd>
          <dt>
            <Translate contentKey="gatewayApp.dataacmRbacAttribute.rbacPolicy">Rbac Policy</Translate>
          </dt>
          <dd>{rbacAttributeEntity.rbacPolicy ? rbacAttributeEntity.rbacPolicy.id : ''}</dd>
          <dt>
            <Translate contentKey="gatewayApp.dataacmRbacAttribute.rbacPermission">Rbac Permission</Translate>
          </dt>
          <dd>{rbacAttributeEntity.rbacPermission ? rbacAttributeEntity.rbacPermission.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/rbac-attribute" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/rbac-attribute/${rbacAttributeEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

const mapStateToProps = ({ rbacAttribute }: IRootState) => ({
  rbacAttributeEntity: rbacAttribute.entity,
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(RbacAttributeDetail);
