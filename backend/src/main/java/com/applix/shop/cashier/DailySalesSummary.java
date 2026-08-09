package com.applix.shop.cashier;

import java.math.BigDecimal;

/**
 * revenue — выручка (сумма чеков)
 * goodsValue — сумма в товаре
 * cashSum / cashlessSum — наличные / безналичные
 * spare — излишек (расхождение факт/ожидание в кассе); null, если день ещё
 *         текущий и снепшот из total для него не считается
 */
public record DailySalesSummary(
        BigDecimal revenue,
        BigDecimal goodsValue,
        BigDecimal cashSum,
        BigDecimal cashlessSum,
        BigDecimal spare
) {
    public DailySalesSummary(BigDecimal revenue, BigDecimal goodsValue, BigDecimal cashSum, BigDecimal cashlessSum) {
        this(revenue, goodsValue, cashSum, cashlessSum, null);
    }
}
