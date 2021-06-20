package com.dbs.itt.security.repository.rowmapper;

import com.dbs.itt.security.domain.RbacDataAccess;
import com.dbs.itt.security.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link RbacDataAccess}, with proper type conversions.
 */
@Service
public class RbacDataAccessRowMapper implements BiFunction<Row, String, RbacDataAccess> {

    private final ColumnConverter converter;

    public RbacDataAccessRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link RbacDataAccess} stored in the database.
     */
    @Override
    public RbacDataAccess apply(Row row, String prefix) {
        RbacDataAccess entity = new RbacDataAccess();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setDesc(converter.fromRow(row, prefix + "_rbac_desc", String.class));
        return entity;
    }
}
