package com.uet.legacy;

public class VipDiscount implements DiscountStrategy {

    private static final double LARGE_ORDER_THRESHOLD = 5000;
    private static final double LARGE_ORDER_RATE = 0.15;
    private static final double STANDARD_RATE = 0.10;

    @Override
    public double calculate(double rawTotal, int lineItemCount) {
        if (rawTotal > LARGE_ORDER_THRESHOLD) {
            return rawTotal * LARGE_ORDER_RATE;
        }
        return rawTotal * STANDARD_RATE;
    }
}