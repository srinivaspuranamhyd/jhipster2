import React from 'react';
import MenuItem from 'app/shared/layout/menus/menu-item';
import { Translate, translate } from 'react-jhipster';
import { NavDropdown } from './menu-components';

export const EntitiesMenu = props => (
  <NavDropdown
    icon="th-list"
    name={translate('global.menu.entities.main')}
    id="entity-menu"
    data-cy="entity"
    style={{ maxHeight: '80vh', overflow: 'auto' }}
  >
    <MenuItem icon="asterisk" to="/rbac-attribute">
      <Translate contentKey="global.menu.entities.dataacmRbacAttribute" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/rbac-attribute-val">
      <Translate contentKey="global.menu.entities.dataacmRbacAttributeVal" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/rbac-data-access">
      <Translate contentKey="global.menu.entities.dataacmRbacDataAccess" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/rbac-permission">
      <Translate contentKey="global.menu.entities.dataacmRbacPermission" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/rbac-permission-approval">
      <Translate contentKey="global.menu.entities.dataacmRbacPermissionApproval" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/rbac-policy">
      <Translate contentKey="global.menu.entities.dataacmRbacPolicy" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/rbac-security-group">
      <Translate contentKey="global.menu.entities.dataacmRbacSecurityGroup" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/rbac-user">
      <Translate contentKey="global.menu.entities.dataacmRbacUser" />
    </MenuItem>
    {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
  </NavDropdown>
);
