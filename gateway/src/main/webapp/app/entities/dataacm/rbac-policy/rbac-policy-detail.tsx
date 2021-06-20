import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './rbac-policy.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IRbacPolicyDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const RbacPolicyDetail = (props: IRbacPolicyDetailProps) => {
  useEffect(() => {
    props.getEntity(props.match.params.id);
  }, []);

  const { rbacPolicyEntity } = props;
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="rbacPolicyDetailsHeading">
          <Translate contentKey="gatewayApp.dataacmRbacPolicy.detail.title">RbacPolicy</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="gatewayApp.dataacmRbacPolicy.id">Id</Translate>
            </span>
          </dt>
          <dd>{rbacPolicyEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="gatewayApp.dataacmRbacPolicy.name">Name</Translate>
            </span>
          </dt>
          <dd>{rbacPolicyEntity.name}</dd>
          <dt>
            <span id="desc">
              <Translate contentKey="gatewayApp.dataacmRbacPolicy.desc">Desc</Translate>
            </span>
          </dt>
          <dd>{rbacPolicyEntity.desc}</dd>
          <dt>
            <Translate contentKey="gatewayApp.dataacmRbacPolicy.user">User</Translate>
          </dt>
          <dd>{rbacPolicyEntity.user ? rbacPolicyEntity.user.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/rbac-policy" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/rbac-policy/${rbacPolicyEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

const mapStateToProps = ({ rbacPolicy }: IRootState) => ({
  rbacPolicyEntity: rbacPolicy.entity,
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(RbacPolicyDetail);
