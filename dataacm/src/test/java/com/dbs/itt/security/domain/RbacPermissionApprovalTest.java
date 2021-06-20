package com.dbs.itt.security.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.dbs.itt.security.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RbacPermissionApprovalTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(RbacPermissionApproval.class);
        RbacPermissionApproval rbacPermissionApproval1 = new RbacPermissionApproval();
        rbacPermissionApproval1.setId(1L);
        RbacPermissionApproval rbacPermissionApproval2 = new RbacPermissionApproval();
        rbacPermissionApproval2.setId(rbacPermissionApproval1.getId());
        assertThat(rbacPermissionApproval1).isEqualTo(rbacPermissionApproval2);
        rbacPermissionApproval2.setId(2L);
        assertThat(rbacPermissionApproval1).isNotEqualTo(rbacPermissionApproval2);
        rbacPermissionApproval1.setId(null);
        assertThat(rbacPermissionApproval1).isNotEqualTo(rbacPermissionApproval2);
    }
}
