package com.uet.legacy;

import java.util.Map;

public class EasyPaisaPayment implements PaymentStrategy {

    private static final int MOBILE_LENGTH = 11;
    private static final String MOBILE_PREFIX = "03";

    @Override
    public PaymentResult pay(Map<String, Object> orderData, double amount) {
        String mobileNo = (String) orderData.getOrDefault("mobileNumber", "");
        if (mobileNo.length() == MOBILE_LENGTH && mobileNo.startsWith(MOBILE_PREFIX)) {
            System.out.println("Sending EasyPaisa OTP push to " + mobileNo + " for PKR " + amount);
            return PaymentResult.approved("PAID");
        }
        System.out.println("EasyPaisa failed: Invalid mobile number.");
        return PaymentResult.rejected();
    }
}