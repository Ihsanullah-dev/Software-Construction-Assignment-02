package com.uet.legacy;

import java.util.Map;

public class CashOnDeliveryPayment implements PaymentStrategy {

    private static final double COD_LIMIT = 25000;

    @Override
    public PaymentResult pay(Map<String, Object> orderData, double amount) {
        if (amount > COD_LIMIT) {
            System.out.println("Cash on delivery not allowed for orders above PKR 25,000.");
            return PaymentResult.rejected();
        }
        return PaymentResult.approved("PENDING_CASH");
    }
}