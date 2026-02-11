package com.edutech.platform.modules.subscription.application.service;

import com.edutech.platform.modules.iam.domain.entity.User;
import com.edutech.platform.modules.iam.infrastructure.repository.UserRepository;
import com.edutech.platform.modules.subscription.application.port.PaymentGatewayPort;
import com.edutech.platform.modules.subscription.domain.entity.PaymentTransaction;
import com.edutech.platform.modules.subscription.domain.entity.StudentSubscription;
import com.edutech.platform.modules.subscription.domain.entity.SubscriptionPlan;
import com.edutech.platform.modules.subscription.infrastructure.repository.PaymentTransactionRepository;
import com.edutech.platform.modules.subscription.infrastructure.repository.StudentSubscriptionRepository;
import com.edutech.platform.modules.subscription.infrastructure.repository.SubscriptionPlanRepository;
import com.edutech.platform.shared.exception.ApiException;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionService {
    private final SubscriptionPlanRepository planRepository;
    private final StudentSubscriptionRepository subscriptionRepository;
    private final PaymentTransactionRepository paymentRepository;
    private final UserRepository userRepository;
    private final PaymentGatewayPort paymentGatewayPort;

    public SubscriptionService(SubscriptionPlanRepository planRepository, StudentSubscriptionRepository subscriptionRepository,
                               PaymentTransactionRepository paymentRepository, UserRepository userRepository,
                               PaymentGatewayPort paymentGatewayPort) {
        this.planRepository = planRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.paymentGatewayPort = paymentGatewayPort;
    }

    public StudentSubscription subscribe(Long userId, String planCode) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ApiException("User not found"));
        SubscriptionPlan plan = planRepository.findByCode(planCode).orElseThrow(() -> new ApiException("Plan not found"));
        String providerRef = paymentGatewayPort.initializePayment(plan.getMonthlyPrice(), planCode, userId);

        PaymentTransaction tx = new PaymentTransaction();
        tx.setAmount(plan.getMonthlyPrice());
        tx.setProviderRef(providerRef);
        tx.setStatus("SUCCESS");
        paymentRepository.save(tx);

        StudentSubscription subscription = new StudentSubscription();
        subscription.setUser(user);
        subscription.setPlan(plan);
        subscription.setStatus("ACTIVE");
        return subscriptionRepository.save(subscription);
    }

    public void cancel(Long userId) {
        StudentSubscription subscription = subscriptionRepository.findByUserIdAndStatus(userId, "ACTIVE")
                .orElseThrow(() -> new ApiException("Active subscription not found"));
        subscription.setStatus("CANCELLED");
        subscriptionRepository.save(subscription);
    }
}
