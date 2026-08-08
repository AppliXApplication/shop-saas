package com.applix.shop.cashier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CaseRecordRow(
        Long id,
        LocalDateTime date,
        String cashierLogin,
        String description,
        BigDecimal cashInSum,
        BigDecimal cashOutSum,
        BigDecimal arrivalSum,
        BigDecimal writeoffSum
) {
}
