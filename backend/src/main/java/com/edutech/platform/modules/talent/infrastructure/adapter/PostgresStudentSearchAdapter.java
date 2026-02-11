package com.edutech.platform.modules.talent.infrastructure.adapter;

import com.edutech.platform.modules.talent.application.port.StudentSearchPort;
import java.util.ArrayList;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class PostgresStudentSearchAdapter implements StudentSearchPort {
    private final JdbcTemplate jdbcTemplate;

    public PostgresStudentSearchAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Long> searchStudentProfileIds(String keyword, String location) {
        String k = keyword == null ? "" : keyword;
        String l = location == null ? "" : location;
        List<Long> ids = jdbcTemplate.queryForList(
                "select id from student_profiles where interests ilike ? and location_preference ilike ?",
                Long.class,
                "%" + k + "%",
                "%" + l + "%"
        );
        return new ArrayList<>(ids);
    }
}
