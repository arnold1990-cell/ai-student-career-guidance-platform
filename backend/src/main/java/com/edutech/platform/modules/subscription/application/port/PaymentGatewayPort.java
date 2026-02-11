package com.edutech.platform.modules.subscription.application.port;

import java.math.BigDecimal;

public interface PaymentGatewayPort {
    String initializePayment(BigDecimal amount, String planCode, Long userId);
}
