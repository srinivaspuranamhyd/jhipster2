package com.dbs.itt.security.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.dbs.itt.security.domain.RbacDataAccess;
import com.dbs.itt.security.domain.RbacSecurityGroup;
import com.dbs.itt.security.repository.rowmapper.RbacSecurityGroupRowMapper;
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
 * Spring Data SQL reactive custom repository implementation for the RbacSecurityGroup entity.
 */
@SuppressWarnings("unused")
class RbacSecurityGroupRepositoryInternalImpl implements RbacSecurityGroupRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final RbacSecurityGroupRowMapper rbacsecuritygroupMapper;

    private static final Table entityTable = Table.aliased("rbac_security_group", EntityManager.ENTITY_ALIAS);

    private static final EntityManager.LinkTable dataTopicLink = new LinkTable(
        "rel_rbac_security_group__data_topic",
        "rbac_security_group_id",
        "data_topic_id"
    );

    public RbacSecurityGroupRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        RbacSecurityGroupRowMapper rbacsecuritygroupMapper
    ) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.rbacsecuritygroupMapper = rbacsecuritygroupMapper;
    }

    @Override
    public Flux<RbacSecurityGroup> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<RbacSecurityGroup> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<RbacSecurityGroup> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = RbacSecurityGroupSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        SelectFromAndJoin selectFrom = Select.builder().select(columns).from(entityTable);

        String select = entityManager.createSelect(selectFrom, RbacSecurityGroup.class, pageable, criteria);
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
    public Flux<RbacSecurityGroup> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<RbacSecurityGroup> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    @Override
    public Mono<RbacSecurityGroup> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<RbacSecurityGroup> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<RbacSecurityGroup> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private RbacSecurityGroup process(Row row, RowMetadata metadata) {
        RbacSecurityGroup entity = rbacsecuritygroupMapper.apply(row, "e");
        return entity;
    }

    @Override
    public <S extends RbacSecurityGroup> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends RbacSecurityGroup> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity).flatMap(savedEntity -> updateRelations(savedEntity));
        } else {
            return update(entity)
                .map(
                    numberOfUpdates -> {
                        if (numberOfUpdates.intValue() <= 0) {
                            throw new IllegalStateException("Unable to update RbacSecurityGroup with id = " + entity.getId());
                        }
                        return entity;
                    }
                )
                .then(updateRelations(entity));
        }
    }

    @Override
    public Mono<Integer> update(RbacSecurityGroup entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }

    @Override
    public Mono<Void> deleteById(Long entityId) {
        return deleteRelations(entityId)
            .then(r2dbcEntityTemplate.delete(RbacSecurityGroup.class).matching(query(where("id").is(entityId))).all().then());
    }

    protected <S extends RbacSecurityGroup> Mono<S> updateRelations(S entity) {
        Mono<Void> result = entityManager
            .updateLinkTable(dataTopicLink, entity.getId(), entity.getDataTopics().stream().map(RbacDataAccess::getId))
            .then();
        return result.thenReturn(entity);
    }

    protected Mono<Void> deleteRelations(Long entityId) {
        return entityManager.deleteFromLinkTable(dataTopicLink, entityId);
    }
}

class RbacSecurityGroupSqlHelper {

    static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("name", table, columnPrefix + "_name"));
        columns.add(Column.aliased("rbac_desc", table, columnPrefix + "_rbac_desc"));

        return columns;
    }
}
