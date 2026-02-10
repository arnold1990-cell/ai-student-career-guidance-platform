package com.edurite.domain.repo;

import com.edurite.domain.model.MessageThread;import org.springframework.data.jpa.repository.JpaRepository;import java.util.*; public interface MessageThreadRepository extends JpaRepository<MessageThread, Long> { List<MessageThread> findByCompanyId(Long companyId); }
