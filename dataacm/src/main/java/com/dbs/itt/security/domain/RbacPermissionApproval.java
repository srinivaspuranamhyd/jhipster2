package com.dbs.itt.security.domain;

import com.dbs.itt.security.domain.enumeration.RbacPermissionApprovalStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A RbacPermissionApproval.
 */
@Table("rbac_permission_approval")
public class RbacPermissionApproval implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @NotNull(message = "must not be null")
    @Column("rbac_desc")
    private String desc;

    @NotNull(message = "must not be null")
    @Pattern(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    @Column("approver_email")
    private String approverEmail;

    @NotNull(message = "must not be null")
    @Column("status")
    private RbacPermissionApprovalStatus status;

    @Transient
    private RbacPermission permission;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RbacPermissionApproval id(Long id) {
        this.id = id;
        return this;
    }

    public String getDesc() {
        return this.desc;
    }

    public RbacPermissionApproval desc(String desc) {
        this.desc = desc;
        return this;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getApproverEmail() {
        return this.approverEmail;
    }

    public RbacPermissionApproval approverEmail(String approverEmail) {
        this.approverEmail = approverEmail;
        return this;
    }

    public void setApproverEmail(String approverEmail) {
        this.approverEmail = approverEmail;
    }

    public RbacPermissionApprovalStatus getStatus() {
        return this.status;
    }

    public RbacPermissionApproval status(RbacPermissionApprovalStatus status) {
        this.status = status;
        return this;
    }

    public void setStatus(RbacPermissionApprovalStatus status) {
        this.status = status;
    }

    public RbacPermission getPermission() {
        return this.permission;
    }

    public RbacPermissionApproval permission(RbacPermission rbacPermission) {
        this.setPermission(rbacPermission);
        return this;
    }

    public void setPermission(RbacPermission rbacPermission) {
        if (this.permission != null) {
            this.permission.setApproval(null);
        }
        if (permission != null) {
            permission.setApproval(this);
        }
        this.permission = rbacPermission;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RbacPermissionApproval)) {
            return false;
        }
        return id != null && id.equals(((RbacPermissionApproval) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RbacPermissionApproval{" +
            "id=" + getId() +
            ", desc='" + getDesc() + "'" +
            ", approverEmail='" + getApproverEmail() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
