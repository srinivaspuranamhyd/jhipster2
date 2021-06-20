package com.dbs.itt.security.repository.rowmapper;

import com.dbs.itt.security.domain.RbacAttributeVal;
import com.dbs.itt.security.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link RbacAttributeVal}, with proper type conversions.
 */
@Service
public class RbacAttributeValRowMapper implements BiFunction<Row, String, RbacAttributeVal> {

    private final ColumnConverter converter;

    public RbacAttributeValRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link RbacAttributeVal} stored in the database.
     */
    @Override
    public RbacAttributeVal apply(Row row, String prefix) {
        RbacAttributeVal entity = new RbacAttributeVal();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setValue(converter.fromRow(row, prefix + "_value", String.class));
        entity.setAttrId(converter.fromRow(row, prefix + "_attr_id", Long.class));
        return entity;
    }
}
