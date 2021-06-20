package com.dbs.itt.security.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.dbs.itt.security.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RbacPermissionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(RbacPermission.class);
        RbacPermission rbacPermission1 = new RbacPermission();
        rbacPermission1.setId(1L);
        RbacPermission rbacPermission2 = new RbacPermission();
        rbacPermission2.setId(rbacPermission1.getId());
        assertThat(rbacPermission1).isEqualTo(rbacPermission2);
        rbacPermission2.setId(2L);
        assertThat(rbacPermission1).isNotEqualTo(rbacPermission2);
        rbacPermission1.setId(null);
        assertThat(rbacPermission1).isNotEqualTo(rbacPermission2);
    }
}
