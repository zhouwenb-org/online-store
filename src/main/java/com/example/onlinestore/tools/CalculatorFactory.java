package com.example.onlinestore.tools;

public class CalculatorFactory {
    private static final CalculatorFactory INSTANCE = new CalculatorFactory();
    public static CalculatorFactory getInstance() {
        return INSTANCE;
    }

    public PriceCalculator getPriceCalculator() {
        return new ItemPriceCalculator();
    }
}
