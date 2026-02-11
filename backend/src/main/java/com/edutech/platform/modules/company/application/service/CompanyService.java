package com.edutech.platform.modules.company.application.service;

import com.edutech.platform.modules.company.domain.entity.CompanyProfile;
import com.edutech.platform.modules.company.domain.entity.VerificationRequest;
import com.edutech.platform.modules.company.infrastructure.repository.CompanyProfileRepository;
import com.edutech.platform.modules.company.infrastructure.repository.VerificationRequestRepository;
import com.edutech.platform.modules.iam.domain.entity.User;
import com.edutech.platform.modules.iam.infrastructure.repository.UserRepository;
import com.edutech.platform.shared.exception.ApiException;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CompanyService {
    private final CompanyProfileRepository companyRepository;
    private final VerificationRequestRepository verificationRepository;
    private final UserRepository userRepository;

    public CompanyService(CompanyProfileRepository companyRepository, VerificationRequestRepository verificationRepository, UserRepository userRepository) {
        this.companyRepository = companyRepository;
        this.verificationRepository = verificationRepository;
        this.userRepository = userRepository;
    }

    public CompanyProfile register(Long userId, String companyName) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ApiException("User not found"));
        CompanyProfile company = companyRepository.findByOwnerUserId(userId).orElseGet(CompanyProfile::new);
        company.setOwnerUser(user);
        company.setCompanyName(companyName);
        company.setStatus("PENDING_VERIFICATION");
        CompanyProfile saved = companyRepository.save(company);
        VerificationRequest vr = new VerificationRequest();
        vr.setCompanyProfile(saved);
        vr.setStatus("PENDING");
        verificationRepository.save(vr);
        return saved;
    }

    public List<VerificationRequest> pendingQueue() {
        return verificationRepository.findByStatus("PENDING");
    }

    public VerificationRequest approve(Long requestId, String decision) {
        VerificationRequest request = verificationRepository.findById(requestId).orElseThrow(() -> new ApiException("Not found"));
        request.setStatus(decision);
        CompanyProfile company = request.getCompanyProfile();
        company.setStatus("APPROVED".equals(decision) ? "VERIFIED" : "REJECTED");
        companyRepository.save(company);
        return verificationRepository.save(request);
    }
}
