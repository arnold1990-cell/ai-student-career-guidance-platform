package com.edurite.integrations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component @Profile("dev")
class DevEmailGateway implements EmailGateway { public void sendEmail(String to, String subject, String body){ log.info("[DEV-EMAIL] to={} subject={} body={}", to, subject, body); } }
@Slf4j
@Component @Profile("dev")
class DevSmsGateway implements SmsGateway { public void sendSms(String to, String message){ log.info("[DEV-SMS] to={} message={}", to, message); } }
@Component @Profile("dev")
class DevPaymentGateway implements PaymentGateway {
    public Map<String,String> createCheckout(Long studentId, String plan){ return Map.of("checkoutUrl", "https://sandbox.pay/checkout/"+studentId+"/"+plan, "providerRef", UUID.randomUUID().toString()); }
    public boolean confirmPaymentWebhook(String providerRef){ return true; }
}
@Component @Profile("dev")
class DevAiRecommendationEngine implements AiRecommendationEngine {
    public List<String> recommendCareers(Long studentId){ return List.of("Software Engineer", "Data Analyst", "Cybersecurity Specialist"); }
    public List<String> recommendBursaries(Long studentId){ return List.of("STEM Future Fund", "Women in Tech Scholarship"); }
    public List<String> recommendCandidates(Long companyId, Long bursaryId){ return List.of("Student #100", "Student #101"); }
    public String skillGapAnalysis(Long studentId){ return "Improve statistics and communication; complete one cloud certification."; }
}
@Component @Profile("dev") class DevDocumentVerificationApi implements DocumentVerificationApi { public boolean verifyDocument(Long documentId){ return true; } }
@Component @Profile("dev") class DevCompanyVerificationApi implements CompanyVerificationApi { public boolean verifyCompany(Long companyId){ return true; } }
