package com.edurite.domain.model;

public class Enums {
    public enum Role {STUDENT, COMPANY, ADMIN}
    public enum UserStatus {ACTIVE, INACTIVE}
    public enum VerificationStatus {PENDING, APPROVED, REJECTED}
    public enum DocumentType {CV, TRANSCRIPT, COMPANY_CERT, OTHER}
    public enum BursaryStatus {DRAFT, PENDING_APPROVAL, ACTIVE, CLOSED, ARCHIVED}
    public enum ApplicationStatus {SAVED, SUBMITTED, UNDER_REVIEW, ACCEPTED, REJECTED}
    public enum SubscriptionName {BASIC, PREMIUM}
    public enum BillingCycle {MONTHLY}
    public enum SubscriptionStatus {ACTIVE, EXPIRED, CANCELLED}
    public enum PaymentStatus {PENDING, SUCCESS, FAILED}
    public enum NotificationChannel {IN_APP, EMAIL, SMS}
    public enum NotificationType {NEW_BURSARY, DEADLINE_REMINDER, CAREER_UPDATE, SYSTEM}
    public enum NotificationStatus {SENT, FAILED, QUEUED}
}
