package com.dbs.itt.security.service.impl;

import com.dbs.itt.security.domain.RbacPermission;
import com.dbs.itt.security.repository.RbacPermissionRepository;
import com.dbs.itt.security.service.RbacPermissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link RbacPermission}.
 */
@Service
@Transactional
public class RbacPermissionServiceImpl implements RbacPermissionService {

    private final Logger log = LoggerFactory.getLogger(RbacPermissionServiceImpl.class);

    private final RbacPermissionRepository rbacPermissionRepository;

    public RbacPermissionServiceImpl(RbacPermissionRepository rbacPermissionRepository) {
        this.rbacPermissionRepository = rbacPermissionRepository;
    }

    @Override
    public Mono<RbacPermission> save(RbacPermission rbacPermission) {
        log.debug("Request to save RbacPermission : {}", rbacPermission);
        return rbacPermissionRepository.save(rbacPermission);
    }

    @Override
    public Mono<RbacPermission> partialUpdate(RbacPermission rbacPermission) {
        log.debug("Request to partially update RbacPermission : {}", rbacPermission);

        return rbacPermissionRepository
            .findById(rbacPermission.getId())
            .map(
                existingRbacPermission -> {
                    if (rbacPermission.getName() != null) {
                        existingRbacPermission.setName(rbacPermission.getName());
                    }
                    if (rbacPermission.getDesc() != null) {
                        existingRbacPermission.setDesc(rbacPermission.getDesc());
                    }

                    return existingRbacPermission;
                }
            )
            .flatMap(rbacPermissionRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<RbacPermission> findAll(Pageable pageable) {
        log.debug("Request to get all RbacPermissions");
        return rbacPermissionRepository.findAllBy(pageable);
    }

    public Mono<Long> countAll() {
        return rbacPermissionRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<RbacPermission> findOne(Long id) {
        log.debug("Request to get RbacPermission : {}", id);
        return rbacPermissionRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete RbacPermission : {}", id);
        return rbacPermissionRepository.deleteById(id);
    }
}
