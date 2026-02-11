package com.edutech.platform.modules.bursary.application.service;

import com.edutech.platform.modules.bursary.domain.entity.Bursary;
import com.edutech.platform.modules.bursary.domain.entity.BursaryApplication;
import com.edutech.platform.modules.bursary.infrastructure.repository.BursaryApplicationRepository;
import com.edutech.platform.modules.bursary.infrastructure.repository.BursaryRepository;
import com.edutech.platform.modules.company.domain.entity.CompanyProfile;
import com.edutech.platform.modules.company.infrastructure.repository.CompanyProfileRepository;
import com.edutech.platform.modules.student.domain.entity.StudentProfile;
import com.edutech.platform.modules.student.infrastructure.repository.StudentProfileRepository;
import com.edutech.platform.shared.exception.ApiException;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class BursaryService {
    private final BursaryRepository bursaryRepository;
    private final CompanyProfileRepository companyProfileRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final BursaryApplicationRepository applicationRepository;

    public BursaryService(BursaryRepository bursaryRepository, CompanyProfileRepository companyProfileRepository,
                          StudentProfileRepository studentProfileRepository, BursaryApplicationRepository applicationRepository) {
        this.bursaryRepository = bursaryRepository;
        this.companyProfileRepository = companyProfileRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.applicationRepository = applicationRepository;
    }

    public Bursary create(Long companyUserId, String title, String description) {
        CompanyProfile company = companyProfileRepository.findByOwnerUserId(companyUserId).orElseThrow(() -> new ApiException("Company not found"));
        Bursary bursary = new Bursary();
        bursary.setCompanyProfile(company);
        bursary.setTitle(title);
        bursary.setDescription(description);
        bursary.setApprovalStatus("PENDING");
        return bursaryRepository.save(bursary);
    }

    public Bursary approve(Long bursaryId, String status) {
        Bursary bursary = bursaryRepository.findById(bursaryId).orElseThrow(() -> new ApiException("Bursary not found"));
        bursary.setApprovalStatus(status);
        return bursaryRepository.save(bursary);
    }

    public List<Bursary> search(String q) {
        String value = q == null ? "" : q;
        return bursaryRepository.findByTitleContainingIgnoreCaseAndApprovalStatus(value, "APPROVED");
    }

    public BursaryApplication apply(Long studentUserId, Long bursaryId) {
        StudentProfile studentProfile = studentProfileRepository.findByUserId(studentUserId).orElseThrow(() -> new ApiException("Profile not found"));
        Bursary bursary = bursaryRepository.findById(bursaryId).orElseThrow(() -> new ApiException("Bursary not found"));
        BursaryApplication application = new BursaryApplication();
        application.setBursary(bursary);
        application.setStudentProfile(studentProfile);
        application.setStatus("APPLIED");
        return applicationRepository.save(application);
    }
}
