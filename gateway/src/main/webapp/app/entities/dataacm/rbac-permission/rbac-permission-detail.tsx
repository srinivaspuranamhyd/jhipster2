import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './rbac-permission.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IRbacPermissionDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const RbacPermissionDetail = (props: IRbacPermissionDetailProps) => {
  useEffect(() => {
    props.getEntity(props.match.params.id);
  }, []);

  const { rbacPermissionEntity } = props;
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="rbacPermissionDetailsHeading">
          <Translate contentKey="gatewayApp.dataacmRbacPermission.detail.title">RbacPermission</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="gatewayApp.dataacmRbacPermission.id">Id</Translate>
            </span>
          </dt>
          <dd>{rbacPermissionEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="gatewayApp.dataacmRbacPermission.name">Name</Translate>
            </span>
          </dt>
          <dd>{rbacPermissionEntity.name}</dd>
          <dt>
            <span id="desc">
              <Translate contentKey="gatewayApp.dataacmRbacPermission.desc">Desc</Translate>
            </span>
          </dt>
          <dd>{rbacPermissionEntity.desc}</dd>
          <dt>
            <Translate contentKey="gatewayApp.dataacmRbacPermission.approval">Approval</Translate>
          </dt>
          <dd>{rbacPermissionEntity.approval ? rbacPermissionEntity.approval.id : ''}</dd>
          <dt>
            <Translate contentKey="gatewayApp.dataacmRbacPermission.user">User</Translate>
          </dt>
          <dd>{rbacPermissionEntity.user ? rbacPermissionEntity.user.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/rbac-permission" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/rbac-permission/${rbacPermissionEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

const mapStateToProps = ({ rbacPermission }: IRootState) => ({
  rbacPermissionEntity: rbacPermission.entity,
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(RbacPermissionDetail);
