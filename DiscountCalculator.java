package com.uet.legacy;

import java.util.HashMap;
import java.util.Map;

/** Chooses the discount rule for a customer type. Unknown types are treated as REGULAR. */
public class DiscountCalculator {

    private final Map<String, DiscountStrategy> strategies = new HashMap<>();
    private final DiscountStrategy defaultStrategy = new RegularDiscount();

    public DiscountCalculator() {
        register("VIP", new VipDiscount());
        register("STUDENT", new StudentDiscount());
        register("CORPORATE", new CorporateDiscount());
        register("REGULAR", defaultStrategy);
    }

    /** Adding a new customer type = one new class + one register call. No existing code edited. */
    public void register(String customerType, DiscountStrategy strategy) {
        strategies.put(customerType, strategy);
    }

    public double calculate(String customerType, double rawTotal, int lineItemCount) {
        return strategies.getOrDefault(customerType, defaultStrategy)
                .calculate(rawTotal, lineItemCount);
    }
}