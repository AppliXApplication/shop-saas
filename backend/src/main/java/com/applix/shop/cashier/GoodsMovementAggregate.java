package com.applix.shop.cashier;

import java.math.BigDecimal;

public record GoodsMovementAggregate(
        Long goodsId,
        BigDecimal quantity,
        BigDecimal sum,
        BigDecimal profit // используется только для продаж, для прихода/списания null
) {
    public GoodsMovementAggregate(Long goodsId, BigDecimal quantity, BigDecimal sum) {
        this(goodsId, quantity, sum, null);
    }
}
