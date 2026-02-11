package com.edutech.platform.modules.talent.application.port;

import java.util.List;

public interface StudentSearchPort {
    List<Long> searchStudentProfileIds(String keyword, String location);
}
