package com.dbs.itt.security.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.dbs.itt.security.domain.RbacPermission;
import com.dbs.itt.security.repository.rowmapper.RbacPermissionApprovalRowMapper;
import com.dbs.itt.security.repository.rowmapper.RbacPermissionRowMapper;
import com.dbs.itt.security.repository.rowmapper.RbacUserRowMapper;
import com.dbs.itt.security.service.EntityManager;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive custom repository implementation for the RbacPermission entity.
 */
@SuppressWarnings("unused")
class RbacPermissionRepositoryInternalImpl implements RbacPermissionRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final RbacPermissionApprovalRowMapper rbacpermissionapprovalMapper;
    private final RbacUserRowMapper rbacuserMapper;
    private final RbacPermissionRowMapper rbacpermissionMapper;

    private static final Table entityTable = Table.aliased("rbac_permission", EntityManager.ENTITY_ALIAS);
    private static final Table approvalTable = Table.aliased("rbac_permission_approval", "approval");
    private static final Table userTable = Table.aliased("rbac_user", "e_user");

    public RbacPermissionRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        RbacPermissionApprovalRowMapper rbacpermissionapprovalMapper,
        RbacUserRowMapper rbacuserMapper,
        RbacPermissionRowMapper rbacpermissionMapper
    ) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.rbacpermissionapprovalMapper = rbacpermissionapprovalMapper;
        this.rbacuserMapper = rbacuserMapper;
        this.rbacpermissionMapper = rbacpermissionMapper;
    }

    @Override
    public Flux<RbacPermission> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<RbacPermission> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<RbacPermission> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = RbacPermissionSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(RbacPermissionApprovalSqlHelper.getColumns(approvalTable, "approval"));
        columns.addAll(RbacUserSqlHelper.getColumns(userTable, "user"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(approvalTable)
            .on(Column.create("approval_id", entityTable))
            .equals(Column.create("id", approvalTable))
            .leftOuterJoin(userTable)
            .on(Column.create("user_id", entityTable))
            .equals(Column.create("id", userTable));

        String select = entityManager.createSelect(selectFrom, RbacPermission.class, pageable, criteria);
        String alias = entityTable.getReferenceName().getReference();
        String selectWhere = Optional
            .ofNullable(criteria)
            .map(
                crit ->
                    new StringBuilder(select)
                        .append(" ")
                        .append("WHERE")
                        .append(" ")
                        .append(alias)
                        .append(".")
                        .append(crit.toString())
                        .toString()
            )
            .orElse(select); // TODO remove once https://github.com/spring-projects/spring-data-jdbc/issues/907 will be fixed
        return db.sql(selectWhere).map(this::process);
    }

    @Override
    public Flux<RbacPermission> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<RbacPermission> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    private RbacPermission process(Row row, RowMetadata metadata) {
        RbacPermission entity = rbacpermissionMapper.apply(row, "e");
        entity.setApproval(rbacpermissionapprovalMapper.apply(row, "approval"));
        entity.setUser(rbacuserMapper.apply(row, "user"));
        return entity;
    }

    @Override
    public <S extends RbacPermission> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends RbacPermission> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity)
                .map(
                    numberOfUpdates -> {
                        if (numberOfUpdates.intValue() <= 0) {
                            throw new IllegalStateException("Unable to update RbacPermission with id = " + entity.getId());
                        }
                        return entity;
                    }
                );
        }
    }

    @Override
    public Mono<Integer> update(RbacPermission entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }
}

class RbacPermissionSqlHelper {

    static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("name", table, columnPrefix + "_name"));
        columns.add(Column.aliased("rbac_desc", table, columnPrefix + "_rbac_desc"));

        columns.add(Column.aliased("approval_id", table, columnPrefix + "_approval_id"));
        columns.add(Column.aliased("user_id", table, columnPrefix + "_user_id"));
        return columns;
    }
}
