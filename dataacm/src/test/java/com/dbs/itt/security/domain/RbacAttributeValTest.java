package com.dbs.itt.security.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.dbs.itt.security.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RbacAttributeValTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(RbacAttributeVal.class);
        RbacAttributeVal rbacAttributeVal1 = new RbacAttributeVal();
        rbacAttributeVal1.setId(1L);
        RbacAttributeVal rbacAttributeVal2 = new RbacAttributeVal();
        rbacAttributeVal2.setId(rbacAttributeVal1.getId());
        assertThat(rbacAttributeVal1).isEqualTo(rbacAttributeVal2);
        rbacAttributeVal2.setId(2L);
        assertThat(rbacAttributeVal1).isNotEqualTo(rbacAttributeVal2);
        rbacAttributeVal1.setId(null);
        assertThat(rbacAttributeVal1).isNotEqualTo(rbacAttributeVal2);
    }
}
