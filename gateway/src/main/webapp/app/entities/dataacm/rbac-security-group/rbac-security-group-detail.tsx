import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './rbac-security-group.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IRbacSecurityGroupDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const RbacSecurityGroupDetail = (props: IRbacSecurityGroupDetailProps) => {
  useEffect(() => {
    props.getEntity(props.match.params.id);
  }, []);

  const { rbacSecurityGroupEntity } = props;
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="rbacSecurityGroupDetailsHeading">
          <Translate contentKey="gatewayApp.dataacmRbacSecurityGroup.detail.title">RbacSecurityGroup</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{rbacSecurityGroupEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="gatewayApp.dataacmRbacSecurityGroup.name">Name</Translate>
            </span>
          </dt>
          <dd>{rbacSecurityGroupEntity.name}</dd>
          <dt>
            <span id="desc">
              <Translate contentKey="gatewayApp.dataacmRbacSecurityGroup.desc">Desc</Translate>
            </span>
          </dt>
          <dd>{rbacSecurityGroupEntity.desc}</dd>
          <dt>
            <Translate contentKey="gatewayApp.dataacmRbacSecurityGroup.dataTopic">Data Topic</Translate>
          </dt>
          <dd>
            {rbacSecurityGroupEntity.dataTopics
              ? rbacSecurityGroupEntity.dataTopics.map((val, i) => (
                  <span key={val.id}>
                    <a>{val.id}</a>
                    {rbacSecurityGroupEntity.dataTopics && i === rbacSecurityGroupEntity.dataTopics.length - 1 ? '' : ', '}
                  </span>
                ))
              : null}
          </dd>
        </dl>
        <Button tag={Link} to="/rbac-security-group" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/rbac-security-group/${rbacSecurityGroupEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

const mapStateToProps = ({ rbacSecurityGroup }: IRootState) => ({
  rbacSecurityGroupEntity: rbacSecurityGroup.entity,
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(RbacSecurityGroupDetail);
