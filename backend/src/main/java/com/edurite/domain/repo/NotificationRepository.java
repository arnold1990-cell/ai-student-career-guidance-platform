package com.edurite.domain.repo;

import com.edurite.domain.model.Notification;import org.springframework.data.jpa.repository.JpaRepository;import java.util.*; public interface NotificationRepository extends JpaRepository<Notification, Long> { List<Notification> findByUserId(Long userId); }
