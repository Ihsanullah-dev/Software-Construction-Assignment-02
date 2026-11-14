package com.uet.legacy;

public class RegularDiscount implements DiscountStrategy {

    private static final double LARGE_ORDER_THRESHOLD = 10000;
    private static final double FLAT_DISCOUNT = 500.0;

    @Override
    public double calculate(double rawTotal, int lineItemCount) {
        if (rawTotal > LARGE_ORDER_THRESHOLD) {
            return FLAT_DISCOUNT;
        }
        return 0.0;
    }
}