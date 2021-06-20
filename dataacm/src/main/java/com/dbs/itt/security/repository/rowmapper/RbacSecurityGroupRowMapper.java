package com.dbs.itt.security.repository.rowmapper;

import com.dbs.itt.security.domain.RbacSecurityGroup;
import com.dbs.itt.security.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link RbacSecurityGroup}, with proper type conversions.
 */
@Service
public class RbacSecurityGroupRowMapper implements BiFunction<Row, String, RbacSecurityGroup> {

    private final ColumnConverter converter;

    public RbacSecurityGroupRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link RbacSecurityGroup} stored in the database.
     */
    @Override
    public RbacSecurityGroup apply(Row row, String prefix) {
        RbacSecurityGroup entity = new RbacSecurityGroup();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setDesc(converter.fromRow(row, prefix + "_rbac_desc", String.class));
        return entity;
    }
}
