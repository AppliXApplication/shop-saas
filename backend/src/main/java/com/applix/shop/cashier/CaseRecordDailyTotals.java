package com.applix.shop.cashier;

import java.math.BigDecimal;

public record CaseRecordDailyTotals(
        BigDecimal cashMustBeTotal,
        BigDecimal cashInTotal,
        BigDecimal cashOutTotal,
        BigDecimal arrivalTotal,
        BigDecimal writeoffTotal,
        long recordCount
) {
}
