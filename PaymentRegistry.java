package com.uet.legacy;

import java.util.HashMap;
import java.util.Map;

/** Finds the right PaymentStrategy for a payment method name. */
public class PaymentRegistry {

    private final Map<String, PaymentStrategy> strategies = new HashMap<>();

    public PaymentRegistry() {
        register("CREDIT_CARD", new CreditCardPayment());
        register("EASYPAISA", new EasyPaisaPayment());
        register("CASH_ON_DELIVERY", new CashOnDeliveryPayment());
    }

    /** Adding a new payment method = one new class + one register call. */
    public void register(String methodName, PaymentStrategy strategy) {
        strategies.put(methodName, strategy);
    }

    public PaymentResult pay(String methodName, Map<String, Object> orderData, double amount) {
        PaymentStrategy strategy = strategies.get(methodName);
        if (strategy == null) {
            System.out.println("Error: Unsupported payment method: " + methodName);
            return PaymentResult.rejected();
        }
        return strategy.pay(orderData, amount);
    }
}