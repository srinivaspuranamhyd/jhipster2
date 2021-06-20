package com.dbs.itt.security.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A RbacAttribute.
 */
@Table("rbac_attribute")
public class RbacAttribute implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @NotNull(message = "must not be null")
    @Column("name")
    private String name;

    @NotNull(message = "must not be null")
    @Column("type")
    private String type;

    @Transient
    @JsonIgnoreProperties(value = { "attr" }, allowSetters = true)
    private Set<RbacAttributeVal> attrvals = new HashSet<>();

    @JsonIgnoreProperties(value = { "attrs", "user" }, allowSetters = true)
    @Transient
    private RbacPolicy rbacPolicy;

    @Column("rbac_policy_id")
    private Long rbacPolicyId;

    @JsonIgnoreProperties(value = { "approval", "attrs", "user" }, allowSetters = true)
    @Transient
    private RbacPermission rbacPermission;

    @Column("rbac_permission_id")
    private Long rbacPermissionId;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RbacAttribute id(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public RbacAttribute name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return this.type;
    }

    public RbacAttribute type(String type) {
        this.type = type;
        return this;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Set<RbacAttributeVal> getAttrvals() {
        return this.attrvals;
    }

    public RbacAttribute attrvals(Set<RbacAttributeVal> rbacAttributeVals) {
        this.setAttrvals(rbacAttributeVals);
        return this;
    }

    public RbacAttribute addAttrval(RbacAttributeVal rbacAttributeVal) {
        this.attrvals.add(rbacAttributeVal);
        rbacAttributeVal.setAttr(this);
        return this;
    }

    public RbacAttribute removeAttrval(RbacAttributeVal rbacAttributeVal) {
        this.attrvals.remove(rbacAttributeVal);
        rbacAttributeVal.setAttr(null);
        return this;
    }

    public void setAttrvals(Set<RbacAttributeVal> rbacAttributeVals) {
        if (this.attrvals != null) {
            this.attrvals.forEach(i -> i.setAttr(null));
        }
        if (rbacAttributeVals != null) {
            rbacAttributeVals.forEach(i -> i.setAttr(this));
        }
        this.attrvals = rbacAttributeVals;
    }

    public RbacPolicy getRbacPolicy() {
        return this.rbacPolicy;
    }

    public RbacAttribute rbacPolicy(RbacPolicy rbacPolicy) {
        this.setRbacPolicy(rbacPolicy);
        this.rbacPolicyId = rbacPolicy != null ? rbacPolicy.getId() : null;
        return this;
    }

    public void setRbacPolicy(RbacPolicy rbacPolicy) {
        this.rbacPolicy = rbacPolicy;
        this.rbacPolicyId = rbacPolicy != null ? rbacPolicy.getId() : null;
    }

    public Long getRbacPolicyId() {
        return this.rbacPolicyId;
    }

    public void setRbacPolicyId(Long rbacPolicy) {
        this.rbacPolicyId = rbacPolicy;
    }

    public RbacPermission getRbacPermission() {
        return this.rbacPermission;
    }

    public RbacAttribute rbacPermission(RbacPermission rbacPermission) {
        this.setRbacPermission(rbacPermission);
        this.rbacPermissionId = rbacPermission != null ? rbacPermission.getId() : null;
        return this;
    }

    public void setRbacPermission(RbacPermission rbacPermission) {
        this.rbacPermission = rbacPermission;
        this.rbacPermissionId = rbacPermission != null ? rbacPermission.getId() : null;
    }

    public Long getRbacPermissionId() {
        return this.rbacPermissionId;
    }

    public void setRbacPermissionId(Long rbacPermission) {
        this.rbacPermissionId = rbacPermission;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RbacAttribute)) {
            return false;
        }
        return id != null && id.equals(((RbacAttribute) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RbacAttribute{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", type='" + getType() + "'" +
            "}";
    }
}
