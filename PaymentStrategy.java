package com.uet.legacy;

import java.util.Map;

/** One way of paying. Each implementation prints its own messages and returns the outcome. */
public interface PaymentStrategy {
    PaymentResult pay(Map<String, Object> orderData, double amount);
}