package com.edurite.domain.repo;

import com.edurite.domain.model.Career;import org.springframework.data.domain.*;import org.springframework.data.jpa.repository.*;import org.springframework.data.repository.query.Param; public interface CareerRepository extends JpaRepository<Career, Long> { @Query("select c from Career c where (:field is null or c.field=:field)") Page<Career> search(@Param("field") String field, Pageable p); }
