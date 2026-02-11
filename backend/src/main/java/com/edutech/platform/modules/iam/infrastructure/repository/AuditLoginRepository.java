package com.edutech.platform.modules.iam.infrastructure.repository;

import com.edutech.platform.modules.iam.domain.entity.AuditLogin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLoginRepository extends JpaRepository<AuditLogin, Long> {
}
