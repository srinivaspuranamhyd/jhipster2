import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './rbac-permission-approval.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IRbacPermissionApprovalDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const RbacPermissionApprovalDetail = (props: IRbacPermissionApprovalDetailProps) => {
  useEffect(() => {
    props.getEntity(props.match.params.id);
  }, []);

  const { rbacPermissionApprovalEntity } = props;
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="rbacPermissionApprovalDetailsHeading">
          <Translate contentKey="gatewayApp.dataacmRbacPermissionApproval.detail.title">RbacPermissionApproval</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="gatewayApp.dataacmRbacPermissionApproval.id">Id</Translate>
            </span>
          </dt>
          <dd>{rbacPermissionApprovalEntity.id}</dd>
          <dt>
            <span id="desc">
              <Translate contentKey="gatewayApp.dataacmRbacPermissionApproval.desc">Desc</Translate>
            </span>
          </dt>
          <dd>{rbacPermissionApprovalEntity.desc}</dd>
          <dt>
            <span id="approverEmail">
              <Translate contentKey="gatewayApp.dataacmRbacPermissionApproval.approverEmail">Approver Email</Translate>
            </span>
          </dt>
          <dd>{rbacPermissionApprovalEntity.approverEmail}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="gatewayApp.dataacmRbacPermissionApproval.status">Status</Translate>
            </span>
          </dt>
          <dd>{rbacPermissionApprovalEntity.status}</dd>
        </dl>
        <Button tag={Link} to="/rbac-permission-approval" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/rbac-permission-approval/${rbacPermissionApprovalEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

const mapStateToProps = ({ rbacPermissionApproval }: IRootState) => ({
  rbacPermissionApprovalEntity: rbacPermissionApproval.entity,
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(RbacPermissionApprovalDetail);
