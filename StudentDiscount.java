package com.uet.legacy;

public class StudentDiscount implements DiscountStrategy {

    private static final double RATE = 0.08;

    @Override
    public double calculate(double rawTotal, int lineItemCount) {
        return rawTotal * RATE;
    }
}