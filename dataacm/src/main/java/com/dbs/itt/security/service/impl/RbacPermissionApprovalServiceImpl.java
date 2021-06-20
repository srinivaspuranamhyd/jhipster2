package com.dbs.itt.security.service.impl;

import com.dbs.itt.security.domain.RbacPermissionApproval;
import com.dbs.itt.security.repository.RbacPermissionApprovalRepository;
import com.dbs.itt.security.service.RbacPermissionApprovalService;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link RbacPermissionApproval}.
 */
@Service
@Transactional
public class RbacPermissionApprovalServiceImpl implements RbacPermissionApprovalService {

    private final Logger log = LoggerFactory.getLogger(RbacPermissionApprovalServiceImpl.class);

    private final RbacPermissionApprovalRepository rbacPermissionApprovalRepository;

    public RbacPermissionApprovalServiceImpl(RbacPermissionApprovalRepository rbacPermissionApprovalRepository) {
        this.rbacPermissionApprovalRepository = rbacPermissionApprovalRepository;
    }

    @Override
    public Mono<RbacPermissionApproval> save(RbacPermissionApproval rbacPermissionApproval) {
        log.debug("Request to save RbacPermissionApproval : {}", rbacPermissionApproval);
        return rbacPermissionApprovalRepository.save(rbacPermissionApproval);
    }

    @Override
    public Mono<RbacPermissionApproval> partialUpdate(RbacPermissionApproval rbacPermissionApproval) {
        log.debug("Request to partially update RbacPermissionApproval : {}", rbacPermissionApproval);

        return rbacPermissionApprovalRepository
            .findById(rbacPermissionApproval.getId())
            .map(
                existingRbacPermissionApproval -> {
                    if (rbacPermissionApproval.getDesc() != null) {
                        existingRbacPermissionApproval.setDesc(rbacPermissionApproval.getDesc());
                    }
                    if (rbacPermissionApproval.getApproverEmail() != null) {
                        existingRbacPermissionApproval.setApproverEmail(rbacPermissionApproval.getApproverEmail());
                    }
                    if (rbacPermissionApproval.getStatus() != null) {
                        existingRbacPermissionApproval.setStatus(rbacPermissionApproval.getStatus());
                    }

                    return existingRbacPermissionApproval;
                }
            )
            .flatMap(rbacPermissionApprovalRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<RbacPermissionApproval> findAll(Pageable pageable) {
        log.debug("Request to get all RbacPermissionApprovals");
        return rbacPermissionApprovalRepository.findAllBy(pageable);
    }

    /**
     *  Get all the rbacPermissionApprovals where Permission is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<RbacPermissionApproval> findAllWherePermissionIsNull() {
        log.debug("Request to get all rbacPermissionApprovals where Permission is null");
        return rbacPermissionApprovalRepository.findAllWherePermissionIsNull();
    }

    public Mono<Long> countAll() {
        return rbacPermissionApprovalRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<RbacPermissionApproval> findOne(Long id) {
        log.debug("Request to get RbacPermissionApproval : {}", id);
        return rbacPermissionApprovalRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete RbacPermissionApproval : {}", id);
        return rbacPermissionApprovalRepository.deleteById(id);
    }
}
