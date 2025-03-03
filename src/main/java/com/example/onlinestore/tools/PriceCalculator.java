package com.example.onlinestore.tools;

import com.example.onlinestore.bean.Item;

import java.math.BigDecimal;

public interface PriceCalculator {
    BigDecimal calculate(Item item);
}

