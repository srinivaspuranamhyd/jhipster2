package com.dbs.itt.security.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.dbs.itt.security.domain.RbacAttributeVal;
import com.dbs.itt.security.repository.rowmapper.RbacAttributeRowMapper;
import com.dbs.itt.security.repository.rowmapper.RbacAttributeValRowMapper;
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
 * Spring Data SQL reactive custom repository implementation for the RbacAttributeVal entity.
 */
@SuppressWarnings("unused")
class RbacAttributeValRepositoryInternalImpl implements RbacAttributeValRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final RbacAttributeRowMapper rbacattributeMapper;
    private final RbacAttributeValRowMapper rbacattributevalMapper;

    private static final Table entityTable = Table.aliased("rbac_attribute_val", EntityManager.ENTITY_ALIAS);
    private static final Table attrTable = Table.aliased("rbac_attribute", "attr");

    public RbacAttributeValRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        RbacAttributeRowMapper rbacattributeMapper,
        RbacAttributeValRowMapper rbacattributevalMapper
    ) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.rbacattributeMapper = rbacattributeMapper;
        this.rbacattributevalMapper = rbacattributevalMapper;
    }

    @Override
    public Flux<RbacAttributeVal> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<RbacAttributeVal> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<RbacAttributeVal> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = RbacAttributeValSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(RbacAttributeSqlHelper.getColumns(attrTable, "attr"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(attrTable)
            .on(Column.create("attr_id", entityTable))
            .equals(Column.create("id", attrTable));

        String select = entityManager.createSelect(selectFrom, RbacAttributeVal.class, pageable, criteria);
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
    public Flux<RbacAttributeVal> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<RbacAttributeVal> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    private RbacAttributeVal process(Row row, RowMetadata metadata) {
        RbacAttributeVal entity = rbacattributevalMapper.apply(row, "e");
        entity.setAttr(rbacattributeMapper.apply(row, "attr"));
        return entity;
    }

    @Override
    public <S extends RbacAttributeVal> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends RbacAttributeVal> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity)
                .map(
                    numberOfUpdates -> {
                        if (numberOfUpdates.intValue() <= 0) {
                            throw new IllegalStateException("Unable to update RbacAttributeVal with id = " + entity.getId());
                        }
                        return entity;
                    }
                );
        }
    }

    @Override
    public Mono<Integer> update(RbacAttributeVal entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }
}

class RbacAttributeValSqlHelper {

    static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("value", table, columnPrefix + "_value"));

        columns.add(Column.aliased("attr_id", table, columnPrefix + "_attr_id"));
        return columns;
    }
}
