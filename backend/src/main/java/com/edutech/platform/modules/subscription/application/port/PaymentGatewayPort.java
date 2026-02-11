package com.edutech.platform.modules.subscription.application.port;

public interface PaymentGatewayPort {
    String initializePayment(double amount, String planCode, Long userId);
}
