package com.dbs.itt.security.repository.rowmapper;

import com.dbs.itt.security.domain.RbacPolicy;
import com.dbs.itt.security.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link RbacPolicy}, with proper type conversions.
 */
@Service
public class RbacPolicyRowMapper implements BiFunction<Row, String, RbacPolicy> {

    private final ColumnConverter converter;

    public RbacPolicyRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link RbacPolicy} stored in the database.
     */
    @Override
    public RbacPolicy apply(Row row, String prefix) {
        RbacPolicy entity = new RbacPolicy();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setDesc(converter.fromRow(row, prefix + "_rbac_desc", String.class));
        entity.setUserId(converter.fromRow(row, prefix + "_user_id", Long.class));
        return entity;
    }
}
