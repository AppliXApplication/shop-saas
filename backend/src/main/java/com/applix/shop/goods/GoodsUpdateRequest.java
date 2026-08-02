package com.applix.shop.goods;

import java.math.BigDecimal;

public record GoodsUpdateRequest(
        String name,
        String code,
        BigDecimal residue,
        BigDecimal price,
        BigDecimal priceOpt,
        Integer categoryId,
        Boolean marking
) {
}
