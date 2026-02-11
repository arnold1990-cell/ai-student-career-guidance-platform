package com.edutech.platform.modules.student.application.service;

import com.edutech.platform.modules.iam.domain.entity.User;
import com.edutech.platform.modules.iam.infrastructure.repository.UserRepository;
import com.edutech.platform.modules.student.api.dto.*;
import com.edutech.platform.modules.student.application.port.StoragePort;
import com.edutech.platform.modules.student.domain.entity.Document;
import com.edutech.platform.modules.student.domain.entity.StudentProfile;
import com.edutech.platform.modules.student.infrastructure.repository.DocumentRepository;
import com.edutech.platform.modules.student.infrastructure.repository.StudentProfileRepository;
import com.edutech.platform.shared.exception.ApiException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class StudentProfileService {
    private final StudentProfileRepository studentProfileRepository;
    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;
    private final StoragePort storagePort;

    public StudentProfileService(StudentProfileRepository studentProfileRepository, UserRepository userRepository,
                                 DocumentRepository documentRepository, StoragePort storagePort) {
        this.studentProfileRepository = studentProfileRepository;
        this.userRepository = userRepository;
        this.documentRepository = documentRepository;
        this.storagePort = storagePort;
    }

    public StudentProfileResponse upsert(Long userId, StudentProfileRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ApiException("User not found"));
        StudentProfile profile = studentProfileRepository.findByUserId(userId).orElseGet(StudentProfile::new);
        profile.setUser(user);
        profile.setInterests(request.getInterests());
        profile.setLocationPreference(request.getLocationPreference());
        profile.setBio(request.getBio());
        StudentProfile saved = studentProfileRepository.save(profile);
        return new StudentProfileResponse(saved.getId(), saved.getInterests(), saved.getLocationPreference(), saved.getBio());
    }

    public StudentProfileResponse getByUser(Long userId) {
        StudentProfile profile = studentProfileRepository.findByUserId(userId).orElseThrow(() -> new ApiException("Profile not found"));
        return new StudentProfileResponse(profile.getId(), profile.getInterests(), profile.getLocationPreference(), profile.getBio());
    }

    public String addDocument(Long userId, DocumentRequest request) {
        if (!"application/pdf".equals(request.getContentType()) && !"image/png".equals(request.getContentType()) && !"image/jpeg".equals(request.getContentType())) {
            throw new ApiException("Unsupported file type");
        }
        StudentProfile profile = studentProfileRepository.findByUserId(userId).orElseThrow(() -> new ApiException("Profile not found"));
        String objectKey = storagePort.allocateObjectKey(request.getFileName());
        Document document = new Document();
        document.setStudentProfile(profile);
        document.setFileName(request.getFileName());
        document.setContentType(request.getContentType());
        document.setSizeBytes(request.getSizeBytes());
        document.setObjectKey(objectKey);
        documentRepository.save(document);
        return storagePort.createSignedUploadUrl(objectKey);
    }

    public List<String> listDocuments(Long userId) {
        StudentProfile profile = studentProfileRepository.findByUserId(userId).orElseThrow(() -> new ApiException("Profile not found"));
        List<Document> docs = documentRepository.findByStudentProfileId(profile.getId());
        List<String> result = new ArrayList<>();
        int i = 0;
        while (i < docs.size()) {
            result.add(docs.get(i).getFileName());
            i++;
        }
        return result;
    }
}
