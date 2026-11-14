package com.uet.legacy;

/** One discount rule. Returns the discount AMOUNT, not the final price. */
public interface DiscountStrategy {
    double calculate(double rawTotal, int lineItemCount);
}