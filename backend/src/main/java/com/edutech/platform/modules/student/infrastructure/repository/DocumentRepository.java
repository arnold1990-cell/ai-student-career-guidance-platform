package com.edutech.platform.modules.student.infrastructure.repository;

import com.edutech.platform.modules.student.domain.entity.Document;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByStudentProfileId(Long studentProfileId);
}
