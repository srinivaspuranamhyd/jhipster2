package com.dbs.itt.security.repository.rowmapper;

import com.dbs.itt.security.domain.RbacPermission;
import com.dbs.itt.security.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link RbacPermission}, with proper type conversions.
 */
@Service
public class RbacPermissionRowMapper implements BiFunction<Row, String, RbacPermission> {

    private final ColumnConverter converter;

    public RbacPermissionRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link RbacPermission} stored in the database.
     */
    @Override
    public RbacPermission apply(Row row, String prefix) {
        RbacPermission entity = new RbacPermission();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setDesc(converter.fromRow(row, prefix + "_rbac_desc", String.class));
        entity.setApprovalId(converter.fromRow(row, prefix + "_approval_id", Long.class));
        entity.setUserId(converter.fromRow(row, prefix + "_user_id", Long.class));
        return entity;
    }
}
