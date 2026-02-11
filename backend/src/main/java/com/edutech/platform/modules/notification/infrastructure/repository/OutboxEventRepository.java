package com.edutech.platform.modules.notification.infrastructure.repository;

import com.edutech.platform.modules.notification.domain.entity.OutboxEvent;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {
    List<OutboxEvent> findTop20ByStatusOrderByCreatedAtAsc(String status);
}
