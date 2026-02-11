package com.edutech.platform.modules.subscription.infrastructure.adapter;

import com.edutech.platform.modules.subscription.application.port.PaymentGatewayPort;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class DummyPaymentGatewayAdapter implements PaymentGatewayPort {
    @Override
    public String initializePayment(double amount, String planCode, Long userId) {
        return "PAY-" + UUID.randomUUID();
    }
}
