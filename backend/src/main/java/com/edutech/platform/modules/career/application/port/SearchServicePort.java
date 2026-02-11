package com.edutech.platform.modules.career.application.port;

import java.util.List;

public interface SearchServicePort {
    List<String> searchCareers(String keyword);
}
