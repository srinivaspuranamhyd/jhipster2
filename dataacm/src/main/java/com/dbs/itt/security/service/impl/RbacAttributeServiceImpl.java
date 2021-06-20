package com.dbs.itt.security.service.impl;

import com.dbs.itt.security.domain.RbacAttribute;
import com.dbs.itt.security.repository.RbacAttributeRepository;
import com.dbs.itt.security.service.RbacAttributeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link RbacAttribute}.
 */
@Service
@Transactional
public class RbacAttributeServiceImpl implements RbacAttributeService {

    private final Logger log = LoggerFactory.getLogger(RbacAttributeServiceImpl.class);

    private final RbacAttributeRepository rbacAttributeRepository;

    public RbacAttributeServiceImpl(RbacAttributeRepository rbacAttributeRepository) {
        this.rbacAttributeRepository = rbacAttributeRepository;
    }

    @Override
    public Mono<RbacAttribute> save(RbacAttribute rbacAttribute) {
        log.debug("Request to save RbacAttribute : {}", rbacAttribute);
        return rbacAttributeRepository.save(rbacAttribute);
    }

    @Override
    public Mono<RbacAttribute> partialUpdate(RbacAttribute rbacAttribute) {
        log.debug("Request to partially update RbacAttribute : {}", rbacAttribute);

        return rbacAttributeRepository
            .findById(rbacAttribute.getId())
            .map(
                existingRbacAttribute -> {
                    if (rbacAttribute.getName() != null) {
                        existingRbacAttribute.setName(rbacAttribute.getName());
                    }
                    if (rbacAttribute.getType() != null) {
                        existingRbacAttribute.setType(rbacAttribute.getType());
                    }

                    return existingRbacAttribute;
                }
            )
            .flatMap(rbacAttributeRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<RbacAttribute> findAll(Pageable pageable) {
        log.debug("Request to get all RbacAttributes");
        return rbacAttributeRepository.findAllBy(pageable);
    }

    public Mono<Long> countAll() {
        return rbacAttributeRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<RbacAttribute> findOne(Long id) {
        log.debug("Request to get RbacAttribute : {}", id);
        return rbacAttributeRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete RbacAttribute : {}", id);
        return rbacAttributeRepository.deleteById(id);
    }
}
