package com.applix.shop.goods;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GoodsDetailController {

    private final GoodsRepository goodsRepository;
    private final CategoryGoodsRepository categoryGoodsRepository;

    @GetMapping("/api/goods/{id}")
    public GoodsDetail get(@PathVariable Long id) {
        Goods goods = goodsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Товар не найден: " + id));
        return GoodsDetail.from(goods);
    }

    /**
     * Полное редактирование карточки товара. Только ROLE_ADMIN — та же политика,
     * что и для быстрого редактирования остатка в таблице.
     */
    @PutMapping("/api/goods/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public GoodsDetail update(@PathVariable Long id, @RequestBody GoodsUpdateRequest request) {
        Goods goods = goodsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Товар не найден: " + id));

        goods.setName(request.name());
        goods.setCode(request.code());
        goods.setResidue(request.residue());
        goods.setPrice(request.price());
        goods.setPriceOpt(request.priceOpt());
        goods.setCategoryId(request.categoryId());
        goods.setMarking(request.marking());

        Goods saved = goodsRepository.save(goods);

        // Явно берём имя категории по id, а не через lazy-связь goods.category —
        // та могла закэшироваться в persistence-контексте до смены categoryId.
        String categoryName = request.categoryId() != null
                ? categoryGoodsRepository.findById(request.categoryId())
                        .map(CategoryGoods::getName)
                        .orElse(null)
                : null;

        return new GoodsDetail(
                saved.getId(), saved.getName(), saved.getCode(), saved.getResidue(),
                saved.getPrice(), saved.getPriceOpt(), saved.getCategoryId(), categoryName,
                saved.getMarking()
        );
    }
}

