package com.dbs.itt.security.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.dbs.itt.security.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RbacSecurityGroupTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(RbacSecurityGroup.class);
        RbacSecurityGroup rbacSecurityGroup1 = new RbacSecurityGroup();
        rbacSecurityGroup1.setId(1L);
        RbacSecurityGroup rbacSecurityGroup2 = new RbacSecurityGroup();
        rbacSecurityGroup2.setId(rbacSecurityGroup1.getId());
        assertThat(rbacSecurityGroup1).isEqualTo(rbacSecurityGroup2);
        rbacSecurityGroup2.setId(2L);
        assertThat(rbacSecurityGroup1).isNotEqualTo(rbacSecurityGroup2);
        rbacSecurityGroup1.setId(null);
        assertThat(rbacSecurityGroup1).isNotEqualTo(rbacSecurityGroup2);
    }
}
