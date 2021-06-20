import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './rbac-data-access.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IRbacDataAccessDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const RbacDataAccessDetail = (props: IRbacDataAccessDetailProps) => {
  useEffect(() => {
    props.getEntity(props.match.params.id);
  }, []);

  const { rbacDataAccessEntity } = props;
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="rbacDataAccessDetailsHeading">
          <Translate contentKey="gatewayApp.dataacmRbacDataAccess.detail.title">RbacDataAccess</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="gatewayApp.dataacmRbacDataAccess.id">Id</Translate>
            </span>
          </dt>
          <dd>{rbacDataAccessEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="gatewayApp.dataacmRbacDataAccess.name">Name</Translate>
            </span>
          </dt>
          <dd>{rbacDataAccessEntity.name}</dd>
          <dt>
            <span id="desc">
              <Translate contentKey="gatewayApp.dataacmRbacDataAccess.desc">Desc</Translate>
            </span>
          </dt>
          <dd>{rbacDataAccessEntity.desc}</dd>
        </dl>
        <Button tag={Link} to="/rbac-data-access" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/rbac-data-access/${rbacDataAccessEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

const mapStateToProps = ({ rbacDataAccess }: IRootState) => ({
  rbacDataAccessEntity: rbacDataAccess.entity,
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(RbacDataAccessDetail);
