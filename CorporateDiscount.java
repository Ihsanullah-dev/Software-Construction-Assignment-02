package com.uet.legacy;

public class CorporateDiscount implements DiscountStrategy {

    private static final int BULK_MIN_LINE_ITEMS = 10;
    private static final double BULK_RATE = 0.20;
    private static final double STANDARD_RATE = 0.05;

    @Override
    public double calculate(double rawTotal, int lineItemCount) {
        if (lineItemCount >= BULK_MIN_LINE_ITEMS) {
            return rawTotal * BULK_RATE;
        }
        return rawTotal * STANDARD_RATE;
    }
}