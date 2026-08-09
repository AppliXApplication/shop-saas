package com.applix.shop.general;

import java.math.BigDecimal;

public record GeneralReportRow(
        Long goodsId,
        String name,
        BigDecimal residue,
        BigDecimal soldQty,
        BigDecimal receivedQty,
        BigDecimal writtenOffQty,
        BigDecimal salesSum,
        BigDecimal arrivalSum,
        BigDecimal writeoffSum,
        BigDecimal profit
) {
}
