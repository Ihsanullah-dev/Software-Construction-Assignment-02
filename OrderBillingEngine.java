package com.uet.legacy;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * LEGACY BILLING ENGINE (being refactored step by step)
 */
public class OrderBillingEngine {

    // Bad Practice 1: Hardcoded file paths and direct file manipulation
    private String logFilePath = "order_audit.log";

    private final DiscountCalculator discountCalculator = new DiscountCalculator();
    private final TaxCalculator taxCalculator = new TaxCalculator();
    private final PaymentRegistry paymentRegistry = new PaymentRegistry();

    public boolean processOrder(Map<String, Object> orderData) {
        // 1. Input Validation (Messy nested validation and console error printing)
        if (orderData == null || orderData.isEmpty()) {
            System.out.println("Error: Order data is empty!");
            return false;
        }

        String email = (String) orderData.get("customerEmail");
        if (email == null || !email.contains("@")) {
            System.out.println("Error: Invalid customer email!");
            return false;
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> items = (List<Map<String, Object>>) orderData.get("items");
        if (items == null || items.isEmpty()) {
            System.out.println("Error: Cart has no items!");
            return false;
        }

        // 2. Total & Discount Calculation
        double rawTotal = 0.0;
        for (Map<String, Object> item : items) {
            if (!item.containsKey("price") || !item.containsKey("quantity")) {
                System.out.println("Error: Missing item price or quantity!");
                return false;
            }
            double price = (double) item.get("price");
            int quantity = (int) item.get("quantity");
            if (price <= 0 || quantity <= 0) {
                System.out.println("Error: Invalid price or quantity for item!");
                return false;
            }
            rawTotal += price * quantity;
        }

        String customerType = (String) orderData.getOrDefault("customerType", "REGULAR");
        double discount = discountCalculator.calculate(customerType, rawTotal, items.size());

        double netAmount = rawTotal - discount;

        String country = (String) orderData.getOrDefault("country", "PK");
        double tax = taxCalculator.calculate(netAmount, country);
        double payableAmount = netAmount + tax;

        // 3. Payment Processing
        String paymentMethod = (String) orderData.getOrDefault("paymentMethod", "CASH");
        PaymentResult paymentResult = paymentRegistry.pay(paymentMethod, orderData, payableAmount);
        if (!paymentResult.isSuccess()) {
            return false;
        }
        String paymentStatus = paymentResult.getStatus();

        // 4. Direct File Saving (Side-effect inside business logic)
        try (FileWriter writer = new FileWriter(logFilePath, true)) {
            writer.write(LocalDateTime.now() + " | Order for: " + email + " | Payable: PKR "
                         + payableAmount + " | Status: " + paymentStatus + "\n");
        } catch (IOException e) {
            System.out.println("Failed to write order log: " + e.getMessage());
        }

        // 5. Direct Notification Dispatch (Coupled side-effect)
        String notifyChannel = (String) orderData.getOrDefault("notifyChannel", "EMAIL");
        if (notifyChannel.equals("EMAIL")) {
            System.out.println("[EMAIL DISPATCHED] To: " + email + " | Your order of PKR "
                               + String.format("%.2f", payableAmount) + " is confirmed!");
        } else if (notifyChannel.equals("SMS")) {
            String phone = (String) orderData.getOrDefault("phone", "0300-0000000");
            System.out.println("[SMS DISPATCHED] To: " + phone + " | Order confirmed! Paid: PKR "
                               + String.format("%.2f", payableAmount));
        }

        return true;
    }

    // Helper getter/setter for testing file path redirection
    public void setLogFilePath(String logFilePath) {
        this.logFilePath = logFilePath;
    }

    public String getLogFilePath() {
        return logFilePath;
    }
}