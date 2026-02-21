package com.example.blackrock.challenge.beans;

public record TaxSlab(long fromInclusive,      // ₹0, 7000001, 10000001, ...
        long toExclusive,        // 7000001, 10000001, 12000001, ...
        double ratePercentage) {

}
