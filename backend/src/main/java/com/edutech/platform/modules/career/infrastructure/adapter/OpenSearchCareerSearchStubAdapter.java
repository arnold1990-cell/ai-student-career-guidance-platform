package com.edutech.platform.modules.career.infrastructure.adapter;

import com.edutech.platform.modules.career.application.port.SearchServicePort;
import java.util.ArrayList;
import java.util.List;

public class OpenSearchCareerSearchStubAdapter implements SearchServicePort {
    @Override
    public List<String> searchCareers(String keyword) {
        return new ArrayList<>();
    }
}
