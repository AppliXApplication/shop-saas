package com.applix.shop.general;

import java.math.BigDecimal;

public record GeneralReportTotals(
        BigDecimal salesSum,
        BigDecimal arrivalSum,
        BigDecimal writeoffSum,
        BigDecimal profit
) {
}
