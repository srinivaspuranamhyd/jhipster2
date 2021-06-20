package com.dbs.itt.security.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.dbs.itt.security.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RbacUserTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(RbacUser.class);
        RbacUser rbacUser1 = new RbacUser();
        rbacUser1.setId(1L);
        RbacUser rbacUser2 = new RbacUser();
        rbacUser2.setId(rbacUser1.getId());
        assertThat(rbacUser1).isEqualTo(rbacUser2);
        rbacUser2.setId(2L);
        assertThat(rbacUser1).isNotEqualTo(rbacUser2);
        rbacUser1.setId(null);
        assertThat(rbacUser1).isNotEqualTo(rbacUser2);
    }
}
