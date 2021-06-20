package com.dbs.itt.security.service.impl;

import com.dbs.itt.security.domain.RbacUser;
import com.dbs.itt.security.repository.RbacUserRepository;
import com.dbs.itt.security.service.RbacUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link RbacUser}.
 */
@Service
@Transactional
public class RbacUserServiceImpl implements RbacUserService {

    private final Logger log = LoggerFactory.getLogger(RbacUserServiceImpl.class);

    private final RbacUserRepository rbacUserRepository;

    public RbacUserServiceImpl(RbacUserRepository rbacUserRepository) {
        this.rbacUserRepository = rbacUserRepository;
    }

    @Override
    public Mono<RbacUser> save(RbacUser rbacUser) {
        log.debug("Request to save RbacUser : {}", rbacUser);
        return rbacUserRepository.save(rbacUser);
    }

    @Override
    public Mono<RbacUser> partialUpdate(RbacUser rbacUser) {
        log.debug("Request to partially update RbacUser : {}", rbacUser);

        return rbacUserRepository
            .findById(rbacUser.getId())
            .map(
                existingRbacUser -> {
                    if (rbacUser.getOnebankId() != null) {
                        existingRbacUser.setOnebankId(rbacUser.getOnebankId());
                    }
                    if (rbacUser.getLanId() != null) {
                        existingRbacUser.setLanId(rbacUser.getLanId());
                    }
                    if (rbacUser.getEmail() != null) {
                        existingRbacUser.setEmail(rbacUser.getEmail());
                    }
                    if (rbacUser.getStatus() != null) {
                        existingRbacUser.setStatus(rbacUser.getStatus());
                    }
                    if (rbacUser.getDepartment() != null) {
                        existingRbacUser.setDepartment(rbacUser.getDepartment());
                    }
                    if (rbacUser.getCountry() != null) {
                        existingRbacUser.setCountry(rbacUser.getCountry());
                    }
                    if (rbacUser.getManagerId() != null) {
                        existingRbacUser.setManagerId(rbacUser.getManagerId());
                    }

                    return existingRbacUser;
                }
            )
            .flatMap(rbacUserRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<RbacUser> findAll(Pageable pageable) {
        log.debug("Request to get all RbacUsers");
        return rbacUserRepository.findAllBy(pageable);
    }

    public Flux<RbacUser> findAllWithEagerRelationships(Pageable pageable) {
        return rbacUserRepository.findAllWithEagerRelationships(pageable);
    }

    public Mono<Long> countAll() {
        return rbacUserRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<RbacUser> findOne(Long id) {
        log.debug("Request to get RbacUser : {}", id);
        return rbacUserRepository.findOneWithEagerRelationships(id);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete RbacUser : {}", id);
        return rbacUserRepository.deleteById(id);
    }
}
