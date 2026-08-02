package com.applix.shop.report;

import java.math.BigDecimal;

/**
 * Строка отчёта "Остатки товаров".
 */
public record GoodsResidueRow(
        Long goodsId,
        String name,
        String code,
        String categoryName,
        BigDecimal residue,
        BigDecimal price
) {
}
