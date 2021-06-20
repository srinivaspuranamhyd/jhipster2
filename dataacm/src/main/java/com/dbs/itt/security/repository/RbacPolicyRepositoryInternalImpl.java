package com.dbs.itt.security.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.dbs.itt.security.domain.RbacPolicy;
import com.dbs.itt.security.repository.rowmapper.RbacPolicyRowMapper;
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
 * Spring Data SQL reactive custom repository implementation for the RbacPolicy entity.
 */
@SuppressWarnings("unused")
class RbacPolicyRepositoryInternalImpl implements RbacPolicyRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final RbacUserRowMapper rbacuserMapper;
    private final RbacPolicyRowMapper rbacpolicyMapper;

    private static final Table entityTable = Table.aliased("rbac_policy", EntityManager.ENTITY_ALIAS);
    private static final Table userTable = Table.aliased("rbac_user", "e_user");

    public RbacPolicyRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        RbacUserRowMapper rbacuserMapper,
        RbacPolicyRowMapper rbacpolicyMapper
    ) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.rbacuserMapper = rbacuserMapper;
        this.rbacpolicyMapper = rbacpolicyMapper;
    }

    @Override
    public Flux<RbacPolicy> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<RbacPolicy> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<RbacPolicy> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = RbacPolicySqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(RbacUserSqlHelper.getColumns(userTable, "user"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(userTable)
            .on(Column.create("user_id", entityTable))
            .equals(Column.create("id", userTable));

        String select = entityManager.createSelect(selectFrom, RbacPolicy.class, pageable, criteria);
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
    public Flux<RbacPolicy> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<RbacPolicy> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    private RbacPolicy process(Row row, RowMetadata metadata) {
        RbacPolicy entity = rbacpolicyMapper.apply(row, "e");
        entity.setUser(rbacuserMapper.apply(row, "user"));
        return entity;
    }

    @Override
    public <S extends RbacPolicy> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends RbacPolicy> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity)
                .map(
                    numberOfUpdates -> {
                        if (numberOfUpdates.intValue() <= 0) {
                            throw new IllegalStateException("Unable to update RbacPolicy with id = " + entity.getId());
                        }
                        return entity;
                    }
                );
        }
    }

    @Override
    public Mono<Integer> update(RbacPolicy entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }
}

class RbacPolicySqlHelper {

    static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("name", table, columnPrefix + "_name"));
        columns.add(Column.aliased("rbac_desc", table, columnPrefix + "_rbac_desc"));

        columns.add(Column.aliased("user_id", table, columnPrefix + "_user_id"));
        return columns;
    }
}
