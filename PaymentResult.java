package com.uet.legacy;

/** Outcome of a payment attempt: did it work, and what status goes in the log. */
public final class PaymentResult {

    private final boolean success;
    private final String status;

    private PaymentResult(boolean success, String status) {
        this.success = success;
        this.status = status;
    }

    public static PaymentResult approved(String status) {
        return new PaymentResult(true, status);
    }

    public static PaymentResult rejected() {
        return new PaymentResult(false, "FAILED");
    }

    public boolean isSuccess() {
        return success;
    }

    public String getStatus() {
        return status;
    }
}