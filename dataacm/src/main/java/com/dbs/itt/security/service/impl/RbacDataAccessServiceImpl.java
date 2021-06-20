package com.dbs.itt.security.service.impl;

import com.dbs.itt.security.domain.RbacDataAccess;
import com.dbs.itt.security.repository.RbacDataAccessRepository;
import com.dbs.itt.security.service.RbacDataAccessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link RbacDataAccess}.
 */
@Service
@Transactional
public class RbacDataAccessServiceImpl implements RbacDataAccessService {

    private final Logger log = LoggerFactory.getLogger(RbacDataAccessServiceImpl.class);

    private final RbacDataAccessRepository rbacDataAccessRepository;

    public RbacDataAccessServiceImpl(RbacDataAccessRepository rbacDataAccessRepository) {
        this.rbacDataAccessRepository = rbacDataAccessRepository;
    }

    @Override
    public Mono<RbacDataAccess> save(RbacDataAccess rbacDataAccess) {
        log.debug("Request to save RbacDataAccess : {}", rbacDataAccess);
        return rbacDataAccessRepository.save(rbacDataAccess);
    }

    @Override
    public Mono<RbacDataAccess> partialUpdate(RbacDataAccess rbacDataAccess) {
        log.debug("Request to partially update RbacDataAccess : {}", rbacDataAccess);

        return rbacDataAccessRepository
            .findById(rbacDataAccess.getId())
            .map(
                existingRbacDataAccess -> {
                    if (rbacDataAccess.getName() != null) {
                        existingRbacDataAccess.setName(rbacDataAccess.getName());
                    }
                    if (rbacDataAccess.getDesc() != null) {
                        existingRbacDataAccess.setDesc(rbacDataAccess.getDesc());
                    }

                    return existingRbacDataAccess;
                }
            )
            .flatMap(rbacDataAccessRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<RbacDataAccess> findAll(Pageable pageable) {
        log.debug("Request to get all RbacDataAccesses");
        return rbacDataAccessRepository.findAllBy(pageable);
    }

    public Mono<Long> countAll() {
        return rbacDataAccessRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<RbacDataAccess> findOne(Long id) {
        log.debug("Request to get RbacDataAccess : {}", id);
        return rbacDataAccessRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete RbacDataAccess : {}", id);
        return rbacDataAccessRepository.deleteById(id);
    }
}
