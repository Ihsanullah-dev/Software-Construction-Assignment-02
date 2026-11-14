package com.uet.legacy;

import java.util.Map;

/** Calculates tax for a net amount based on country. */
public class TaxCalculator {

    private static final double DEFAULT_TAX_RATE = 0.15;
    private static final double PK_TAX_RATE = 0.17;
    private static final double US_TAX_RATE = 0.08;
    private static final double UK_TAX_RATE = 0.20;

    private final Map<String, Double> ratesByCountry = Map.of(
            "PK", PK_TAX_RATE,
            "US", US_TAX_RATE,
            "UK", UK_TAX_RATE
    );

    /** Returns the tax amount (not the total) for the given net amount. */
    public double calculate(double netAmount, String country) {
        double rate = ratesByCountry.getOrDefault(country, DEFAULT_TAX_RATE);
        return netAmount * rate;
    }
}