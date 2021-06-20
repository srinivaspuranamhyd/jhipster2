package com.dbs.itt.security.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.dbs.itt.security.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RbacDataAccessTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(RbacDataAccess.class);
        RbacDataAccess rbacDataAccess1 = new RbacDataAccess();
        rbacDataAccess1.setId(1L);
        RbacDataAccess rbacDataAccess2 = new RbacDataAccess();
        rbacDataAccess2.setId(rbacDataAccess1.getId());
        assertThat(rbacDataAccess1).isEqualTo(rbacDataAccess2);
        rbacDataAccess2.setId(2L);
        assertThat(rbacDataAccess1).isNotEqualTo(rbacDataAccess2);
        rbacDataAccess1.setId(null);
        assertThat(rbacDataAccess1).isNotEqualTo(rbacDataAccess2);
    }
}
