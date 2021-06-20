package com.dbs.itt.security.domain;

import com.dbs.itt.security.domain.enumeration.RbacUserStatus;
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
 * A RbacUser.
 */
@Table("rbac_user")
public class RbacUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @NotNull(message = "must not be null")
    @Column("onebank_id")
    private String onebankId;

    @NotNull(message = "must not be null")
    @Column("lan_id")
    private String lanId;

    @NotNull(message = "must not be null")
    @Pattern(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    @Column("email")
    private String email;

    @NotNull(message = "must not be null")
    @Column("status")
    private RbacUserStatus status;

    @NotNull(message = "must not be null")
    @Column("department")
    private String department;

    @NotNull(message = "must not be null")
    @Column("country")
    private String country;

    @Column("manager_id")
    private String managerId;

    @Transient
    @JsonIgnoreProperties(value = { "attrs", "user" }, allowSetters = true)
    private Set<RbacPolicy> policies = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "approval", "attrs", "user" }, allowSetters = true)
    private Set<RbacPermission> permissions = new HashSet<>();

    @JsonIgnoreProperties(value = { "dataTopics", "users" }, allowSetters = true)
    @Transient
    private Set<RbacSecurityGroup> groups = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RbacUser id(Long id) {
        this.id = id;
        return this;
    }

    public String getOnebankId() {
        return this.onebankId;
    }

    public RbacUser onebankId(String onebankId) {
        this.onebankId = onebankId;
        return this;
    }

    public void setOnebankId(String onebankId) {
        this.onebankId = onebankId;
    }

    public String getLanId() {
        return this.lanId;
    }

    public RbacUser lanId(String lanId) {
        this.lanId = lanId;
        return this;
    }

    public void setLanId(String lanId) {
        this.lanId = lanId;
    }

    public String getEmail() {
        return this.email;
    }

    public RbacUser email(String email) {
        this.email = email;
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public RbacUserStatus getStatus() {
        return this.status;
    }

    public RbacUser status(RbacUserStatus status) {
        this.status = status;
        return this;
    }

    public void setStatus(RbacUserStatus status) {
        this.status = status;
    }

    public String getDepartment() {
        return this.department;
    }

    public RbacUser department(String department) {
        this.department = department;
        return this;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getCountry() {
        return this.country;
    }

    public RbacUser country(String country) {
        this.country = country;
        return this;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getManagerId() {
        return this.managerId;
    }

    public RbacUser managerId(String managerId) {
        this.managerId = managerId;
        return this;
    }

    public void setManagerId(String managerId) {
        this.managerId = managerId;
    }

    public Set<RbacPolicy> getPolicies() {
        return this.policies;
    }

    public RbacUser policies(Set<RbacPolicy> rbacPolicies) {
        this.setPolicies(rbacPolicies);
        return this;
    }

    public RbacUser addPolicy(RbacPolicy rbacPolicy) {
        this.policies.add(rbacPolicy);
        rbacPolicy.setUser(this);
        return this;
    }

    public RbacUser removePolicy(RbacPolicy rbacPolicy) {
        this.policies.remove(rbacPolicy);
        rbacPolicy.setUser(null);
        return this;
    }

    public void setPolicies(Set<RbacPolicy> rbacPolicies) {
        if (this.policies != null) {
            this.policies.forEach(i -> i.setUser(null));
        }
        if (rbacPolicies != null) {
            rbacPolicies.forEach(i -> i.setUser(this));
        }
        this.policies = rbacPolicies;
    }

    public Set<RbacPermission> getPermissions() {
        return this.permissions;
    }

    public RbacUser permissions(Set<RbacPermission> rbacPermissions) {
        this.setPermissions(rbacPermissions);
        return this;
    }

    public RbacUser addPermission(RbacPermission rbacPermission) {
        this.permissions.add(rbacPermission);
        rbacPermission.setUser(this);
        return this;
    }

    public RbacUser removePermission(RbacPermission rbacPermission) {
        this.permissions.remove(rbacPermission);
        rbacPermission.setUser(null);
        return this;
    }

    public void setPermissions(Set<RbacPermission> rbacPermissions) {
        if (this.permissions != null) {
            this.permissions.forEach(i -> i.setUser(null));
        }
        if (rbacPermissions != null) {
            rbacPermissions.forEach(i -> i.setUser(this));
        }
        this.permissions = rbacPermissions;
    }

    public Set<RbacSecurityGroup> getGroups() {
        return this.groups;
    }

    public RbacUser groups(Set<RbacSecurityGroup> rbacSecurityGroups) {
        this.setGroups(rbacSecurityGroups);
        return this;
    }

    public RbacUser addGroup(RbacSecurityGroup rbacSecurityGroup) {
        this.groups.add(rbacSecurityGroup);
        rbacSecurityGroup.getUsers().add(this);
        return this;
    }

    public RbacUser removeGroup(RbacSecurityGroup rbacSecurityGroup) {
        this.groups.remove(rbacSecurityGroup);
        rbacSecurityGroup.getUsers().remove(this);
        return this;
    }

    public void setGroups(Set<RbacSecurityGroup> rbacSecurityGroups) {
        this.groups = rbacSecurityGroups;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RbacUser)) {
            return false;
        }
        return id != null && id.equals(((RbacUser) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RbacUser{" +
            "id=" + getId() +
            ", onebankId='" + getOnebankId() + "'" +
            ", lanId='" + getLanId() + "'" +
            ", email='" + getEmail() + "'" +
            ", status='" + getStatus() + "'" +
            ", department='" + getDepartment() + "'" +
            ", country='" + getCountry() + "'" +
            ", managerId='" + getManagerId() + "'" +
            "}";
    }
}
