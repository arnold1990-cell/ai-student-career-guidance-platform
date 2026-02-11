package com.edutech.platform.modules.student.infrastructure.adapter;

import com.edutech.platform.modules.student.application.port.StoragePort;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class MinioStorageAdapter implements StoragePort {
    @Override
    public String allocateObjectKey(String fileName) {
        return "student-docs/" + UUID.randomUUID() + "-" + fileName;
    }

    @Override
    public String createSignedUploadUrl(String objectKey) {
        return "http://localhost:9000/upload/" + objectKey;
    }
}
