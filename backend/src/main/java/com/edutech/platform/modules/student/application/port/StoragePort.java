package com.edutech.platform.modules.student.application.port;

public interface StoragePort {
    String allocateObjectKey(String fileName);
    String createSignedUploadUrl(String objectKey);
}
