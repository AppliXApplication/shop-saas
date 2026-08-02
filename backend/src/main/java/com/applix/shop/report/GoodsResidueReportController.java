package com.applix.shop.report;

import com.applix.shop.goods.CategoryDTO;
import com.applix.shop.goods.CategoryGoodsRepository;
import com.applix.shop.goods.Goods;
import com.applix.shop.goods.GoodsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class GoodsResidueReportController {

    private static final int MAX_PAGE_SIZE = 200;

    private final GoodsRepository goodsRepository;
    private final CategoryGoodsRepository categoryGoodsRepository;

    @GetMapping("/api/reports/goods-residue")
    public Page<GoodsResidueRow> goodsResidue(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        int safeSize = Math.min(size, MAX_PAGE_SIZE);
        String searchParam = (search == null || search.isBlank()) ? null : search;
        return goodsRepository.findResidueReport(searchParam, categoryId, PageRequest.of(page, safeSize));
    }

    /**
     * Сумма (кол-во * цена) по всей выборке с учётом фильтра категории —
     * не по одной странице, а по всему набору данных.
     * GET /api/reports/goods-residue/sum?categoryId=3
     */
    @GetMapping("/api/reports/goods-residue/sum")
    public BigDecimal goodsResidueSum(@RequestParam(required = false) Integer categoryId) {
        return goodsRepository.sumResidueValue(categoryId);
    }

    @GetMapping("/api/goods/categories")
    public List<CategoryDTO> categories() {
        return categoryGoodsRepository.findAllForFilter();
    }

    @PutMapping("/api/goods/{id}/residue")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> updateResidue(
            @PathVariable Long id,
            @RequestBody UpdateResidueRequest request
    ) {
        Goods goods = goodsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Товар не найден: " + id));
        goods.setResidue(request.residue());
        goodsRepository.save(goods);
        return ResponseEntity.noContent().build();
    }
}



