package com.dbs.itt.security.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.dbs.itt.security.domain.RbacSecurityGroup;
import com.dbs.itt.security.domain.RbacUser;
import com.dbs.itt.security.domain.enumeration.RbacUserStatus;
import com.dbs.itt.security.repository.rowmapper.RbacUserRowMapper;
import com.dbs.itt.security.service.EntityManager;
import com.dbs.itt.security.service.EntityManager.LinkTable;
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
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoin;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive custom repository implementation for the RbacUser entity.
 */
@SuppressWarnings("unused")
class RbacUserRepositoryInternalImpl implements RbacUserRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final RbacUserRowMapper rbacuserMapper;

    private static final Table entityTable = Table.aliased("rbac_user", EntityManager.ENTITY_ALIAS);

    private static final EntityManager.LinkTable groupLink = new LinkTable("rel_rbac_user__group", "rbac_user_id", "group_id");

    public RbacUserRepositoryInternalImpl(R2dbcEntityTemplate template, EntityManager entityManager, RbacUserRowMapper rbacuserMapper) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.rbacuserMapper = rbacuserMapper;
    }

    @Override
    public Flux<RbacUser> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<RbacUser> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<RbacUser> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = RbacUserSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        SelectFromAndJoin selectFrom = Select.builder().select(columns).from(entityTable);

        String select = entityManager.createSelect(selectFrom, RbacUser.class, pageable, criteria);
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
    public Flux<RbacUser> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<RbacUser> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    @Override
    public Mono<RbacUser> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<RbacUser> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<RbacUser> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private RbacUser process(Row row, RowMetadata metadata) {
        RbacUser entity = rbacuserMapper.apply(row, "e");
        return entity;
    }

    @Override
    public <S extends RbacUser> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends RbacUser> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity).flatMap(savedEntity -> updateRelations(savedEntity));
        } else {
            return update(entity)
                .map(
                    numberOfUpdates -> {
                        if (numberOfUpdates.intValue() <= 0) {
                            throw new IllegalStateException("Unable to update RbacUser with id = " + entity.getId());
                        }
                        return entity;
                    }
                )
                .then(updateRelations(entity));
        }
    }

    @Override
    public Mono<Integer> update(RbacUser entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }

    @Override
    public Mono<Void> deleteById(Long entityId) {
        return deleteRelations(entityId)
            .then(r2dbcEntityTemplate.delete(RbacUser.class).matching(query(where("id").is(entityId))).all().then());
    }

    protected <S extends RbacUser> Mono<S> updateRelations(S entity) {
        Mono<Void> result = entityManager
            .updateLinkTable(groupLink, entity.getId(), entity.getGroups().stream().map(RbacSecurityGroup::getId))
            .then();
        return result.thenReturn(entity);
    }

    protected Mono<Void> deleteRelations(Long entityId) {
        return entityManager.deleteFromLinkTable(groupLink, entityId);
    }
}

class RbacUserSqlHelper {

    static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("onebank_id", table, columnPrefix + "_onebank_id"));
        columns.add(Column.aliased("lan_id", table, columnPrefix + "_lan_id"));
        columns.add(Column.aliased("email", table, columnPrefix + "_email"));
        columns.add(Column.aliased("status", table, columnPrefix + "_status"));
        columns.add(Column.aliased("department", table, columnPrefix + "_department"));
        columns.add(Column.aliased("country", table, columnPrefix + "_country"));
        columns.add(Column.aliased("manager_id", table, columnPrefix + "_manager_id"));

        return columns;
    }
}
