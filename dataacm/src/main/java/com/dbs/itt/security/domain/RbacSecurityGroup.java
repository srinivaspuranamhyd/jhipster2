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
 * A RbacSecurityGroup.
 */
@Table("rbac_security_group")
public class RbacSecurityGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @NotNull(message = "must not be null")
    @Column("name")
    private String name;

    @NotNull(message = "must not be null")
    @Column("rbac_desc")
    private String desc;

    @JsonIgnoreProperties(value = { "groups" }, allowSetters = true)
    @Transient
    private Set<RbacDataAccess> dataTopics = new HashSet<>();

    @JsonIgnoreProperties(value = { "policies", "permissions", "groups" }, allowSetters = true)
    @Transient
    private Set<RbacUser> users = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RbacSecurityGroup id(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public RbacSecurityGroup name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return this.desc;
    }

    public RbacSecurityGroup desc(String desc) {
        this.desc = desc;
        return this;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Set<RbacDataAccess> getDataTopics() {
        return this.dataTopics;
    }

    public RbacSecurityGroup dataTopics(Set<RbacDataAccess> rbacDataAccesses) {
        this.setDataTopics(rbacDataAccesses);
        return this;
    }

    public RbacSecurityGroup addDataTopic(RbacDataAccess rbacDataAccess) {
        this.dataTopics.add(rbacDataAccess);
        rbacDataAccess.getGroups().add(this);
        return this;
    }

    public RbacSecurityGroup removeDataTopic(RbacDataAccess rbacDataAccess) {
        this.dataTopics.remove(rbacDataAccess);
        rbacDataAccess.getGroups().remove(this);
        return this;
    }

    public void setDataTopics(Set<RbacDataAccess> rbacDataAccesses) {
        this.dataTopics = rbacDataAccesses;
    }

    public Set<RbacUser> getUsers() {
        return this.users;
    }

    public RbacSecurityGroup users(Set<RbacUser> rbacUsers) {
        this.setUsers(rbacUsers);
        return this;
    }

    public RbacSecurityGroup addUser(RbacUser rbacUser) {
        this.users.add(rbacUser);
        rbacUser.getGroups().add(this);
        return this;
    }

    public RbacSecurityGroup removeUser(RbacUser rbacUser) {
        this.users.remove(rbacUser);
        rbacUser.getGroups().remove(this);
        return this;
    }

    public void setUsers(Set<RbacUser> rbacUsers) {
        if (this.users != null) {
            this.users.forEach(i -> i.removeGroup(this));
        }
        if (rbacUsers != null) {
            rbacUsers.forEach(i -> i.addGroup(this));
        }
        this.users = rbacUsers;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RbacSecurityGroup)) {
            return false;
        }
        return id != null && id.equals(((RbacSecurityGroup) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RbacSecurityGroup{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", desc='" + getDesc() + "'" +
            "}";
    }
}
