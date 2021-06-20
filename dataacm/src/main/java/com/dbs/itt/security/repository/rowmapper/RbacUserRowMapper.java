package com.dbs.itt.security.repository.rowmapper;

import com.dbs.itt.security.domain.RbacUser;
import com.dbs.itt.security.domain.enumeration.RbacUserStatus;
import com.dbs.itt.security.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link RbacUser}, with proper type conversions.
 */
@Service
public class RbacUserRowMapper implements BiFunction<Row, String, RbacUser> {

    private final ColumnConverter converter;

    public RbacUserRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link RbacUser} stored in the database.
     */
    @Override
    public RbacUser apply(Row row, String prefix) {
        RbacUser entity = new RbacUser();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setOnebankId(converter.fromRow(row, prefix + "_onebank_id", String.class));
        entity.setLanId(converter.fromRow(row, prefix + "_lan_id", String.class));
        entity.setEmail(converter.fromRow(row, prefix + "_email", String.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", RbacUserStatus.class));
        entity.setDepartment(converter.fromRow(row, prefix + "_department", String.class));
        entity.setCountry(converter.fromRow(row, prefix + "_country", String.class));
        entity.setManagerId(converter.fromRow(row, prefix + "_manager_id", String.class));
        return entity;
    }
}
