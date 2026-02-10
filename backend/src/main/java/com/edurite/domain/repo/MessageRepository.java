package com.edurite.domain.repo;

import com.edurite.domain.model.Message;import org.springframework.data.jpa.repository.JpaRepository;import java.util.*; public interface MessageRepository extends JpaRepository<Message, Long> { List<Message> findByThreadId(Long threadId); }
