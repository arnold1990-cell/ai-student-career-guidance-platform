package com.edutech.platform.modules.notification.infrastructure.repository;

import com.edutech.platform.modules.notification.domain.entity.Notification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserId(Long userId);
}
