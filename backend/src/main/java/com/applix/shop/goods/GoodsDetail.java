package com.applix.shop.goods;

import java.math.BigDecimal;

public record GoodsDetail(
        Long id,
        String name,
        String code,
        BigDecimal residue,
        BigDecimal price,
        BigDecimal priceOpt,
        Integer categoryId,
        String categoryName,
        Boolean marking
) {
    static GoodsDetail from(Goods g) {
        return new GoodsDetail(
                g.getId(), g.getName(), g.getCode(), g.getResidue(), g.getPrice(),
                g.getPriceOpt(), g.getCategoryId(),
                g.getCategory() != null ? g.getCategory().getName() : null,
                g.getMarking()
        );
    }
}
