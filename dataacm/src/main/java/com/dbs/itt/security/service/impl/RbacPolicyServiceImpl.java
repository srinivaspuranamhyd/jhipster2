package com.dbs.itt.security.service.impl;

import com.dbs.itt.security.domain.RbacPolicy;
import com.dbs.itt.security.repository.RbacPolicyRepository;
import com.dbs.itt.security.service.RbacPolicyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link RbacPolicy}.
 */
@Service
@Transactional
public class RbacPolicyServiceImpl implements RbacPolicyService {

    private final Logger log = LoggerFactory.getLogger(RbacPolicyServiceImpl.class);

    private final RbacPolicyRepository rbacPolicyRepository;

    public RbacPolicyServiceImpl(RbacPolicyRepository rbacPolicyRepository) {
        this.rbacPolicyRepository = rbacPolicyRepository;
    }

    @Override
    public Mono<RbacPolicy> save(RbacPolicy rbacPolicy) {
        log.debug("Request to save RbacPolicy : {}", rbacPolicy);
        return rbacPolicyRepository.save(rbacPolicy);
    }

    @Override
    public Mono<RbacPolicy> partialUpdate(RbacPolicy rbacPolicy) {
        log.debug("Request to partially update RbacPolicy : {}", rbacPolicy);

        return rbacPolicyRepository
            .findById(rbacPolicy.getId())
            .map(
                existingRbacPolicy -> {
                    if (rbacPolicy.getName() != null) {
                        existingRbacPolicy.setName(rbacPolicy.getName());
                    }
                    if (rbacPolicy.getDesc() != null) {
                        existingRbacPolicy.setDesc(rbacPolicy.getDesc());
                    }

                    return existingRbacPolicy;
                }
            )
            .flatMap(rbacPolicyRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<RbacPolicy> findAll(Pageable pageable) {
        log.debug("Request to get all RbacPolicies");
        return rbacPolicyRepository.findAllBy(pageable);
    }

    public Mono<Long> countAll() {
        return rbacPolicyRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<RbacPolicy> findOne(Long id) {
        log.debug("Request to get RbacPolicy : {}", id);
        return rbacPolicyRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete RbacPolicy : {}", id);
        return rbacPolicyRepository.deleteById(id);
    }
}
