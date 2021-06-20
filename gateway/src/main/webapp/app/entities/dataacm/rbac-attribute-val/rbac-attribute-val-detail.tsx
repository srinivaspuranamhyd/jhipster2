import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './rbac-attribute-val.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IRbacAttributeValDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const RbacAttributeValDetail = (props: IRbacAttributeValDetailProps) => {
  useEffect(() => {
    props.getEntity(props.match.params.id);
  }, []);

  const { rbacAttributeValEntity } = props;
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="rbacAttributeValDetailsHeading">
          <Translate contentKey="gatewayApp.dataacmRbacAttributeVal.detail.title">RbacAttributeVal</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="gatewayApp.dataacmRbacAttributeVal.id">Id</Translate>
            </span>
          </dt>
          <dd>{rbacAttributeValEntity.id}</dd>
          <dt>
            <span id="value">
              <Translate contentKey="gatewayApp.dataacmRbacAttributeVal.value">Value</Translate>
            </span>
          </dt>
          <dd>{rbacAttributeValEntity.value}</dd>
          <dt>
            <Translate contentKey="gatewayApp.dataacmRbacAttributeVal.attr">Attr</Translate>
          </dt>
          <dd>{rbacAttributeValEntity.attr ? rbacAttributeValEntity.attr.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/rbac-attribute-val" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/rbac-attribute-val/${rbacAttributeValEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

const mapStateToProps = ({ rbacAttributeVal }: IRootState) => ({
  rbacAttributeValEntity: rbacAttributeVal.entity,
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(RbacAttributeValDetail);
