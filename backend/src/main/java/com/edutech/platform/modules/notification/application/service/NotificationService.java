package com.edutech.platform.modules.notification.application.service;

import com.edutech.platform.modules.iam.domain.entity.User;
import com.edutech.platform.modules.iam.infrastructure.repository.UserRepository;
import com.edutech.platform.modules.notification.domain.entity.Notification;
import com.edutech.platform.modules.notification.domain.entity.OutboxEvent;
import com.edutech.platform.modules.notification.infrastructure.repository.NotificationRepository;
import com.edutech.platform.modules.notification.infrastructure.repository.OutboxEventRepository;
import com.edutech.platform.shared.exception.ApiException;
import java.util.List;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository, OutboxEventRepository outboxEventRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.outboxEventRepository = outboxEventRepository;
        this.userRepository = userRepository;
    }

    public void publishInApp(Long userId, String title, String body) {
        OutboxEvent outboxEvent = new OutboxEvent();
        outboxEvent.setAggregateType("USER");
        outboxEvent.setAggregateId(userId);
        outboxEvent.setEventType("IN_APP_NOTIFICATION");
        outboxEvent.setPayload(title + "||" + body);
        outboxEvent.setStatus("PENDING");
        outboxEventRepository.save(outboxEvent);
    }

    public List<Notification> listForUser(Long userId) {
        return notificationRepository.findByUserId(userId);
    }

    @Scheduled(fixedDelay = 10000)
    @Transactional
    public void processOutbox() {
        List<OutboxEvent> events = outboxEventRepository.findTop20ByStatusOrderByCreatedAtAsc("PENDING");
        int index = 0;
        while (index < events.size()) {
            OutboxEvent event = events.get(index);
            User user = userRepository.findById(event.getAggregateId()).orElseThrow(() -> new ApiException("User not found"));
            String[] parts = event.getPayload().split("\\|\\|");
            Notification notification = new Notification();
            notification.setUser(user);
            notification.setTitle(parts.length > 0 ? parts[0] : "Notification");
            notification.setBody(parts.length > 1 ? parts[1] : "Message");
            notification.setReadStatus(false);
            notificationRepository.save(notification);
            event.setStatus("PROCESSED");
            outboxEventRepository.save(event);
            index++;
        }
    }
}
