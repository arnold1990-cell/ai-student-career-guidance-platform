package com.edurite.application.service;

import com.edurite.domain.model.*;
import com.edurite.domain.model.Enums.*;
import com.edurite.domain.repo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Service @RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepo; private final CompanyProfileRepository companyRepo; private final BursaryRepository bursaryRepo;
    private final AuditLogRepository auditRepo; private final TemplateRepository templateRepo; private final StudentSubscriptionRepository subRepo; private final PaymentTransactionRepository payRepo; private final PasswordEncoder encoder;

    public Page<User> users(Pageable p){ return userRepo.findAll(p); }
    public User createUser(User user){ user.setPasswordHash(encoder.encode(user.getPasswordHash())); user.setCreatedAt(Instant.now()); return userRepo.save(user); }
    public User updateUser(Long id, User user){ user.setId(id); return userRepo.save(user); }
    public void deleteUser(Long id){ userRepo.deleteById(id); }
    public int bulkUpload(MultipartFile file) throws IOException {int c=0; try(var br=new BufferedReader(new InputStreamReader(file.getInputStream()))){String line; while((line=br.readLine())!=null){ if(line.startsWith("email")) continue; String[] p=line.split(","); createUser(User.builder().email(p[0]).passwordHash(p[1]).phone(p.length>2?p[2]:null).role(Role.valueOf(p.length>3?p[3]:"STUDENT")).status(UserStatus.ACTIVE).build()); c++; }} return c;}
    public Page<AuditLog> audit(Pageable p){ return auditRepo.findAll(p); }
    public List<CompanyProfile> pendingCompanies(){ return companyRepo.findByVerificationStatus(VerificationStatus.PENDING); }
    public CompanyProfile approveCompany(Long id){ var c=companyRepo.findById(id).orElseThrow(); c.setVerificationStatus(VerificationStatus.APPROVED); c.setReviewedAt(Instant.now()); return companyRepo.save(c); }
    public CompanyProfile rejectCompany(Long id, String notes){ var c=companyRepo.findById(id).orElseThrow(); c.setVerificationStatus(VerificationStatus.REJECTED); c.setReviewNotes(notes); c.setReviewedAt(Instant.now()); return companyRepo.save(c); }
    public Page<Bursary> pendingBursaries(Pageable p){ return bursaryRepo.byStatus(BursaryStatus.PENDING_APPROVAL, p); }
    public Bursary approveBursary(Long id){ var b=bursaryRepo.findById(id).orElseThrow(); b.setStatus(BursaryStatus.ACTIVE); return bursaryRepo.save(b); }
    public Bursary rejectBursary(Long id){ var b=bursaryRepo.findById(id).orElseThrow(); b.setStatus(BursaryStatus.DRAFT); return bursaryRepo.save(b); }
    public Page<StudentSubscription> subscriptions(Pageable p){ return subRepo.findAll(p); }
    public Page<PaymentTransaction> payments(Pageable p){ return payRepo.findAll(p); }
    public Map<String,Object> monthlyReport(){
        long students=userRepo.findAll().stream().filter(u->u.getRole()==Role.STUDENT).count();
        long companies=userRepo.findAll().stream().filter(u->u.getRole()==Role.COMPANY).count();
        BigDecimal revenue=payRepo.findAll().stream().filter(p->p.getStatus()==PaymentStatus.SUCCESS).map(PaymentTransaction::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        return Map.of("students",students,"companies",companies,"revenue",revenue);
    }
    public List<Template> templates(){ return templateRepo.findAll(); }
    public Template createTemplate(Template t){ return templateRepo.save(t); }
    public Template updateTemplate(Long id, Template t){ t.setId(id); return templateRepo.save(t); }
    public void deleteTemplate(Long id){ templateRepo.deleteById(id); }
}
