package com.uet.legacy;

import java.util.Map;

public class CreditCardPayment implements PaymentStrategy {

    private static final int CARD_LENGTH = 16;

    @Override
    public PaymentResult pay(Map<String, Object> orderData, double amount) {
        String cardNumber = (String) orderData.getOrDefault("cardNumber", "");
        if (cardNumber.length() == CARD_LENGTH && cardNumber.matches("\\d+")) {
            System.out.println("Processing credit card payment of PKR " + amount);
            return PaymentResult.approved("PAID");
        }
        System.out.println("Card declined: Must be 16 digits.");
        return PaymentResult.rejected();
    }
}