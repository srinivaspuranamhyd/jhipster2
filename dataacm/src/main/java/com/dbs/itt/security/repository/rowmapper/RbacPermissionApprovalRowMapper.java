package com.dbs.itt.security.repository.rowmapper;

import com.dbs.itt.security.domain.RbacPermissionApproval;
import com.dbs.itt.security.domain.enumeration.RbacPermissionApprovalStatus;
import com.dbs.itt.security.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link RbacPermissionApproval}, with proper type conversions.
 */
@Service
public class RbacPermissionApprovalRowMapper implements BiFunction<Row, String, RbacPermissionApproval> {

    private final ColumnConverter converter;

    public RbacPermissionApprovalRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link RbacPermissionApproval} stored in the database.
     */
    @Override
    public RbacPermissionApproval apply(Row row, String prefix) {
        RbacPermissionApproval entity = new RbacPermissionApproval();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setDesc(converter.fromRow(row, prefix + "_rbac_desc", String.class));
        entity.setApproverEmail(converter.fromRow(row, prefix + "_approver_email", String.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", RbacPermissionApprovalStatus.class));
        return entity;
    }
}
