package com.dbs.itt.security.repository.rowmapper;

import com.dbs.itt.security.domain.RbacAttribute;
import com.dbs.itt.security.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link RbacAttribute}, with proper type conversions.
 */
@Service
public class RbacAttributeRowMapper implements BiFunction<Row, String, RbacAttribute> {

    private final ColumnConverter converter;

    public RbacAttributeRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link RbacAttribute} stored in the database.
     */
    @Override
    public RbacAttribute apply(Row row, String prefix) {
        RbacAttribute entity = new RbacAttribute();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setType(converter.fromRow(row, prefix + "_type", String.class));
        entity.setRbacPolicyId(converter.fromRow(row, prefix + "_rbac_policy_id", Long.class));
        entity.setRbacPermissionId(converter.fromRow(row, prefix + "_rbac_permission_id", Long.class));
        return entity;
    }
}
