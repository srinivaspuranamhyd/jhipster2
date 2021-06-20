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
 * A RbacDataAccess.
 */
@Table("rbac_data_access")
public class RbacDataAccess implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @NotNull(message = "must not be null")
    @Column("name")
    private String name;

    @NotNull(message = "must not be null")
    @Column("rbac_desc")
    private String desc;

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

    public RbacDataAccess id(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public RbacDataAccess name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return this.desc;
    }

    public RbacDataAccess desc(String desc) {
        this.desc = desc;
        return this;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Set<RbacSecurityGroup> getGroups() {
        return this.groups;
    }

    public RbacDataAccess groups(Set<RbacSecurityGroup> rbacSecurityGroups) {
        this.setGroups(rbacSecurityGroups);
        return this;
    }

    public RbacDataAccess addGroup(RbacSecurityGroup rbacSecurityGroup) {
        this.groups.add(rbacSecurityGroup);
        rbacSecurityGroup.getDataTopics().add(this);
        return this;
    }

    public RbacDataAccess removeGroup(RbacSecurityGroup rbacSecurityGroup) {
        this.groups.remove(rbacSecurityGroup);
        rbacSecurityGroup.getDataTopics().remove(this);
        return this;
    }

    public void setGroups(Set<RbacSecurityGroup> rbacSecurityGroups) {
        if (this.groups != null) {
            this.groups.forEach(i -> i.removeDataTopic(this));
        }
        if (rbacSecurityGroups != null) {
            rbacSecurityGroups.forEach(i -> i.addDataTopic(this));
        }
        this.groups = rbacSecurityGroups;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RbacDataAccess)) {
            return false;
        }
        return id != null && id.equals(((RbacDataAccess) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RbacDataAccess{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", desc='" + getDesc() + "'" +
            "}";
    }
}
