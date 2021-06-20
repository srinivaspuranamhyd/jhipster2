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
 * A RbacPolicy.
 */
@Table("rbac_policy")
public class RbacPolicy implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @NotNull(message = "must not be null")
    @Column("name")
    private String name;

    @NotNull(message = "must not be null")
    @Column("rbac_desc")
    private String desc;

    @Transient
    @JsonIgnoreProperties(value = { "attrvals", "rbacPolicy", "rbacPermission" }, allowSetters = true)
    private Set<RbacAttribute> attrs = new HashSet<>();

    @JsonIgnoreProperties(value = { "policies", "permissions", "groups" }, allowSetters = true)
    @Transient
    private RbacUser user;

    @Column("user_id")
    private Long userId;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RbacPolicy id(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public RbacPolicy name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return this.desc;
    }

    public RbacPolicy desc(String desc) {
        this.desc = desc;
        return this;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Set<RbacAttribute> getAttrs() {
        return this.attrs;
    }

    public RbacPolicy attrs(Set<RbacAttribute> rbacAttributes) {
        this.setAttrs(rbacAttributes);
        return this;
    }

    public RbacPolicy addAttr(RbacAttribute rbacAttribute) {
        this.attrs.add(rbacAttribute);
        rbacAttribute.setRbacPolicy(this);
        return this;
    }

    public RbacPolicy removeAttr(RbacAttribute rbacAttribute) {
        this.attrs.remove(rbacAttribute);
        rbacAttribute.setRbacPolicy(null);
        return this;
    }

    public void setAttrs(Set<RbacAttribute> rbacAttributes) {
        if (this.attrs != null) {
            this.attrs.forEach(i -> i.setRbacPolicy(null));
        }
        if (rbacAttributes != null) {
            rbacAttributes.forEach(i -> i.setRbacPolicy(this));
        }
        this.attrs = rbacAttributes;
    }

    public RbacUser getUser() {
        return this.user;
    }

    public RbacPolicy user(RbacUser rbacUser) {
        this.setUser(rbacUser);
        this.userId = rbacUser != null ? rbacUser.getId() : null;
        return this;
    }

    public void setUser(RbacUser rbacUser) {
        this.user = rbacUser;
        this.userId = rbacUser != null ? rbacUser.getId() : null;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long rbacUser) {
        this.userId = rbacUser;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RbacPolicy)) {
            return false;
        }
        return id != null && id.equals(((RbacPolicy) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RbacPolicy{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", desc='" + getDesc() + "'" +
            "}";
    }
}
