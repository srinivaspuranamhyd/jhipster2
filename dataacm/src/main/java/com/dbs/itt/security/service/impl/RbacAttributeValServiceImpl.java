package com.dbs.itt.security.service.impl;

import com.dbs.itt.security.domain.RbacAttributeVal;
import com.dbs.itt.security.repository.RbacAttributeValRepository;
import com.dbs.itt.security.service.RbacAttributeValService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link RbacAttributeVal}.
 */
@Service
@Transactional
public class RbacAttributeValServiceImpl implements RbacAttributeValService {

    private final Logger log = LoggerFactory.getLogger(RbacAttributeValServiceImpl.class);

    private final RbacAttributeValRepository rbacAttributeValRepository;

    public RbacAttributeValServiceImpl(RbacAttributeValRepository rbacAttributeValRepository) {
        this.rbacAttributeValRepository = rbacAttributeValRepository;
    }

    @Override
    public Mono<RbacAttributeVal> save(RbacAttributeVal rbacAttributeVal) {
        log.debug("Request to save RbacAttributeVal : {}", rbacAttributeVal);
        return rbacAttributeValRepository.save(rbacAttributeVal);
    }

    @Override
    public Mono<RbacAttributeVal> partialUpdate(RbacAttributeVal rbacAttributeVal) {
        log.debug("Request to partially update RbacAttributeVal : {}", rbacAttributeVal);

        return rbacAttributeValRepository
            .findById(rbacAttributeVal.getId())
            .map(
                existingRbacAttributeVal -> {
                    if (rbacAttributeVal.getValue() != null) {
                        existingRbacAttributeVal.setValue(rbacAttributeVal.getValue());
                    }

                    return existingRbacAttributeVal;
                }
            )
            .flatMap(rbacAttributeValRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<RbacAttributeVal> findAll(Pageable pageable) {
        log.debug("Request to get all RbacAttributeVals");
        return rbacAttributeValRepository.findAllBy(pageable);
    }

    public Mono<Long> countAll() {
        return rbacAttributeValRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<RbacAttributeVal> findOne(Long id) {
        log.debug("Request to get RbacAttributeVal : {}", id);
        return rbacAttributeValRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete RbacAttributeVal : {}", id);
        return rbacAttributeValRepository.deleteById(id);
    }
}
