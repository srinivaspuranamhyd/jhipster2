package com.dbs.itt.security.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.dbs.itt.security.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RbacAttributeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(RbacAttribute.class);
        RbacAttribute rbacAttribute1 = new RbacAttribute();
        rbacAttribute1.setId(1L);
        RbacAttribute rbacAttribute2 = new RbacAttribute();
        rbacAttribute2.setId(rbacAttribute1.getId());
        assertThat(rbacAttribute1).isEqualTo(rbacAttribute2);
        rbacAttribute2.setId(2L);
        assertThat(rbacAttribute1).isNotEqualTo(rbacAttribute2);
        rbacAttribute1.setId(null);
        assertThat(rbacAttribute1).isNotEqualTo(rbacAttribute2);
    }
}
