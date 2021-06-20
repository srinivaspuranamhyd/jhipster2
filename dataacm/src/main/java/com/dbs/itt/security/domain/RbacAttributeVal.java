package com.dbs.itt.security.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A RbacAttributeVal.
 */
@Table("rbac_attribute_val")
public class RbacAttributeVal implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @NotNull(message = "must not be null")
    @Column("value")
    private String value;

    @JsonIgnoreProperties(value = { "attrvals", "rbacPolicy", "rbacPermission" }, allowSetters = true)
    @Transient
    private RbacAttribute attr;

    @Column("attr_id")
    private Long attrId;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RbacAttributeVal id(Long id) {
        this.id = id;
        return this;
    }

    public String getValue() {
        return this.value;
    }

    public RbacAttributeVal value(String value) {
        this.value = value;
        return this;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public RbacAttribute getAttr() {
        return this.attr;
    }

    public RbacAttributeVal attr(RbacAttribute rbacAttribute) {
        this.setAttr(rbacAttribute);
        this.attrId = rbacAttribute != null ? rbacAttribute.getId() : null;
        return this;
    }

    public void setAttr(RbacAttribute rbacAttribute) {
        this.attr = rbacAttribute;
        this.attrId = rbacAttribute != null ? rbacAttribute.getId() : null;
    }

    public Long getAttrId() {
        return this.attrId;
    }

    public void setAttrId(Long rbacAttribute) {
        this.attrId = rbacAttribute;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RbacAttributeVal)) {
            return false;
        }
        return id != null && id.equals(((RbacAttributeVal) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RbacAttributeVal{" +
            "id=" + getId() +
            ", value='" + getValue() + "'" +
            "}";
    }
}
