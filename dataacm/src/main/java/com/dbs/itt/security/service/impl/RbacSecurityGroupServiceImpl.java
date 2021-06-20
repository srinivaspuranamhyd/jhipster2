package com.dbs.itt.security.service.impl;

import com.dbs.itt.security.domain.RbacSecurityGroup;
import com.dbs.itt.security.repository.RbacSecurityGroupRepository;
import com.dbs.itt.security.service.RbacSecurityGroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link RbacSecurityGroup}.
 */
@Service
@Transactional
public class RbacSecurityGroupServiceImpl implements RbacSecurityGroupService {

    private final Logger log = LoggerFactory.getLogger(RbacSecurityGroupServiceImpl.class);

    private final RbacSecurityGroupRepository rbacSecurityGroupRepository;

    public RbacSecurityGroupServiceImpl(RbacSecurityGroupRepository rbacSecurityGroupRepository) {
        this.rbacSecurityGroupRepository = rbacSecurityGroupRepository;
    }

    @Override
    public Mono<RbacSecurityGroup> save(RbacSecurityGroup rbacSecurityGroup) {
        log.debug("Request to save RbacSecurityGroup : {}", rbacSecurityGroup);
        return rbacSecurityGroupRepository.save(rbacSecurityGroup);
    }

    @Override
    public Mono<RbacSecurityGroup> partialUpdate(RbacSecurityGroup rbacSecurityGroup) {
        log.debug("Request to partially update RbacSecurityGroup : {}", rbacSecurityGroup);

        return rbacSecurityGroupRepository
            .findById(rbacSecurityGroup.getId())
            .map(
                existingRbacSecurityGroup -> {
                    if (rbacSecurityGroup.getName() != null) {
                        existingRbacSecurityGroup.setName(rbacSecurityGroup.getName());
                    }
                    if (rbacSecurityGroup.getDesc() != null) {
                        existingRbacSecurityGroup.setDesc(rbacSecurityGroup.getDesc());
                    }

                    return existingRbacSecurityGroup;
                }
            )
            .flatMap(rbacSecurityGroupRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<RbacSecurityGroup> findAll(Pageable pageable) {
        log.debug("Request to get all RbacSecurityGroups");
        return rbacSecurityGroupRepository.findAllBy(pageable);
    }

    public Flux<RbacSecurityGroup> findAllWithEagerRelationships(Pageable pageable) {
        return rbacSecurityGroupRepository.findAllWithEagerRelationships(pageable);
    }

    public Mono<Long> countAll() {
        return rbacSecurityGroupRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<RbacSecurityGroup> findOne(Long id) {
        log.debug("Request to get RbacSecurityGroup : {}", id);
        return rbacSecurityGroupRepository.findOneWithEagerRelationships(id);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete RbacSecurityGroup : {}", id);
        return rbacSecurityGroupRepository.deleteById(id);
    }
}
