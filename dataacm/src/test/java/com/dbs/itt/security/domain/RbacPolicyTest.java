package com.dbs.itt.security.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.dbs.itt.security.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RbacPolicyTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(RbacPolicy.class);
        RbacPolicy rbacPolicy1 = new RbacPolicy();
        rbacPolicy1.setId(1L);
        RbacPolicy rbacPolicy2 = new RbacPolicy();
        rbacPolicy2.setId(rbacPolicy1.getId());
        assertThat(rbacPolicy1).isEqualTo(rbacPolicy2);
        rbacPolicy2.setId(2L);
        assertThat(rbacPolicy1).isNotEqualTo(rbacPolicy2);
        rbacPolicy1.setId(null);
        assertThat(rbacPolicy1).isNotEqualTo(rbacPolicy2);
    }
}
