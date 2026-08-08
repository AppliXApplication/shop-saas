package com.applix.shop.cashier;

import java.math.BigDecimal;

/**
 * revenue — выручка (сумма чеков)
 * goodsValue — себестоимость проданного товара (amount*price - profit по строкам чеков)
 * cashSum / cashlessSum — наличные / безналичные из чеков
 */
public record DailySalesSummary(
        BigDecimal revenue,
        BigDecimal goodsValue,
        BigDecimal cashSum,
        BigDecimal cashlessSum
) {
}
