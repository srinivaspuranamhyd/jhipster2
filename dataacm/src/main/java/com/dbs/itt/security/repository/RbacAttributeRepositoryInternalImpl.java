package com.dbs.itt.security.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.dbs.itt.security.domain.RbacAttribute;
import com.dbs.itt.security.repository.rowmapper.RbacAttributeRowMapper;
import com.dbs.itt.security.repository.rowmapper.RbacPermissionRowMapper;
import com.dbs.itt.security.repository.rowmapper.RbacPolicyRowMapper;
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
 * Spring Data SQL reactive custom repository implementation for the RbacAttribute entity.
 */
@SuppressWarnings("unused")
class RbacAttributeRepositoryInternalImpl implements RbacAttributeRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final RbacPolicyRowMapper rbacpolicyMapper;
    private final RbacPermissionRowMapper rbacpermissionMapper;
    private final RbacAttributeRowMapper rbacattributeMapper;

    private static final Table entityTable = Table.aliased("rbac_attribute", EntityManager.ENTITY_ALIAS);
    private static final Table rbacPolicyTable = Table.aliased("rbac_policy", "rbacPolicy");
    private static final Table rbacPermissionTable = Table.aliased("rbac_permission", "rbacPermission");

    public RbacAttributeRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        RbacPolicyRowMapper rbacpolicyMapper,
        RbacPermissionRowMapper rbacpermissionMapper,
        RbacAttributeRowMapper rbacattributeMapper
    ) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.rbacpolicyMapper = rbacpolicyMapper;
        this.rbacpermissionMapper = rbacpermissionMapper;
        this.rbacattributeMapper = rbacattributeMapper;
    }

    @Override
    public Flux<RbacAttribute> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<RbacAttribute> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<RbacAttribute> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = RbacAttributeSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(RbacPolicySqlHelper.getColumns(rbacPolicyTable, "rbacPolicy"));
        columns.addAll(RbacPermissionSqlHelper.getColumns(rbacPermissionTable, "rbacPermission"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(rbacPolicyTable)
            .on(Column.create("rbac_policy_id", entityTable))
            .equals(Column.create("id", rbacPolicyTable))
            .leftOuterJoin(rbacPermissionTable)
            .on(Column.create("rbac_permission_id", entityTable))
            .equals(Column.create("id", rbacPermissionTable));

        String select = entityManager.createSelect(selectFrom, RbacAttribute.class, pageable, criteria);
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
    public Flux<RbacAttribute> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<RbacAttribute> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    private RbacAttribute process(Row row, RowMetadata metadata) {
        RbacAttribute entity = rbacattributeMapper.apply(row, "e");
        entity.setRbacPolicy(rbacpolicyMapper.apply(row, "rbacPolicy"));
        entity.setRbacPermission(rbacpermissionMapper.apply(row, "rbacPermission"));
        return entity;
    }

    @Override
    public <S extends RbacAttribute> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends RbacAttribute> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity)
                .map(
                    numberOfUpdates -> {
                        if (numberOfUpdates.intValue() <= 0) {
                            throw new IllegalStateException("Unable to update RbacAttribute with id = " + entity.getId());
                        }
                        return entity;
                    }
                );
        }
    }

    @Override
    public Mono<Integer> update(RbacAttribute entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }
}

class RbacAttributeSqlHelper {

    static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("name", table, columnPrefix + "_name"));
        columns.add(Column.aliased("type", table, columnPrefix + "_type"));

        columns.add(Column.aliased("rbac_policy_id", table, columnPrefix + "_rbac_policy_id"));
        columns.add(Column.aliased("rbac_permission_id", table, columnPrefix + "_rbac_permission_id"));
        return columns;
    }
}
