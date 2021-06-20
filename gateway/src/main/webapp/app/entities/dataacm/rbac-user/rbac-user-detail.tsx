import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './rbac-user.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IRbacUserDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const RbacUserDetail = (props: IRbacUserDetailProps) => {
  useEffect(() => {
    props.getEntity(props.match.params.id);
  }, []);

  const { rbacUserEntity } = props;
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="rbacUserDetailsHeading">
          <Translate contentKey="gatewayApp.dataacmRbacUser.detail.title">RbacUser</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{rbacUserEntity.id}</dd>
          <dt>
            <span id="onebankId">
              <Translate contentKey="gatewayApp.dataacmRbacUser.onebankId">Onebank Id</Translate>
            </span>
          </dt>
          <dd>{rbacUserEntity.onebankId}</dd>
          <dt>
            <span id="lanId">
              <Translate contentKey="gatewayApp.dataacmRbacUser.lanId">Lan Id</Translate>
            </span>
          </dt>
          <dd>{rbacUserEntity.lanId}</dd>
          <dt>
            <span id="email">
              <Translate contentKey="gatewayApp.dataacmRbacUser.email">Email</Translate>
            </span>
          </dt>
          <dd>{rbacUserEntity.email}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="gatewayApp.dataacmRbacUser.status">Status</Translate>
            </span>
          </dt>
          <dd>{rbacUserEntity.status}</dd>
          <dt>
            <span id="department">
              <Translate contentKey="gatewayApp.dataacmRbacUser.department">Department</Translate>
            </span>
          </dt>
          <dd>{rbacUserEntity.department}</dd>
          <dt>
            <span id="country">
              <Translate contentKey="gatewayApp.dataacmRbacUser.country">Country</Translate>
            </span>
          </dt>
          <dd>{rbacUserEntity.country}</dd>
          <dt>
            <span id="managerId">
              <Translate contentKey="gatewayApp.dataacmRbacUser.managerId">Manager Id</Translate>
            </span>
          </dt>
          <dd>{rbacUserEntity.managerId}</dd>
          <dt>
            <Translate contentKey="gatewayApp.dataacmRbacUser.group">Group</Translate>
          </dt>
          <dd>
            {rbacUserEntity.groups
              ? rbacUserEntity.groups.map((val, i) => (
                  <span key={val.id}>
                    <a>{val.id}</a>
                    {rbacUserEntity.groups && i === rbacUserEntity.groups.length - 1 ? '' : ', '}
                  </span>
                ))
              : null}
          </dd>
        </dl>
        <Button tag={Link} to="/rbac-user" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/rbac-user/${rbacUserEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

const mapStateToProps = ({ rbacUser }: IRootState) => ({
  rbacUserEntity: rbacUser.entity,
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(RbacUserDetail);
