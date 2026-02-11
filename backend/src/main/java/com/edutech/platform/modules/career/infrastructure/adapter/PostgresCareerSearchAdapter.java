package com.edutech.platform.modules.career.infrastructure.adapter;

import com.edutech.platform.modules.career.application.port.SearchServicePort;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class PostgresCareerSearchAdapter implements SearchServicePort {
    private final JdbcTemplate jdbcTemplate;

    public PostgresCareerSearchAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<String> searchCareers(String keyword) {
        String value = keyword == null ? "" : keyword;
        return jdbcTemplate.queryForList("select title from careers where title ilike ?", String.class, "%" + value + "%");
    }
}
