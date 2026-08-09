package com.applix.shop.general;

import com.applix.shop.cashier.ArrivalListRepository;
import com.applix.shop.cashier.CheckListRepository;
import com.applix.shop.cashier.GoodsMovementAggregate;
import com.applix.shop.cashier.WriteoffListRepository;
import com.applix.shop.goods.GoodsRepository;
import com.applix.shop.report.GoodsResidueRow;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class GeneralReportController {

    private static final int MAX_PAGE_SIZE = 200;
    private static final BigDecimal ZERO = BigDecimal.ZERO;

    private final GoodsRepository goodsRepository;
    private final CheckListRepository checkListRepository;
    private final ArrivalListRepository arrivalListRepository;
    private final WriteoffListRepository writeoffListRepository;

    /**
     * Движение товара за период: остаток (текущий), продано/оприходовано/списано
     * (кол-во и суммы) за выбранный диапазон дат, с фильтром по категории.
     * GET /api/reports/general?from=2026-08-01&to=2026-08-31&categoryId=3&page=0&size=50
     * Без from/to — по умолчанию сегодняшний день.
     */
    @GetMapping("/api/reports/general")
    public Page<GeneralReportRow> report(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        LocalDate fromDate = from != null ? from : LocalDate.now();
        LocalDate toDate = to != null ? to : LocalDate.now();
        LocalDateTime fromDateTime = fromDate.atStartOfDay();
        LocalDateTime toDateTime = toDate.plusDays(1).atStartOfDay();

        int safeSize = Math.min(size, MAX_PAGE_SIZE);
        Page<GoodsResidueRow> goodsPage = goodsRepository.findResidueReport(
                null, categoryId, PageRequest.of(page, safeSize)
        );

        List<Long> goodsIds = goodsPage.getContent().stream().map(GoodsResidueRow::goodsId).toList();

        Map<Long, GoodsMovementAggregate> sales = goodsIds.isEmpty() ? Map.of() : toMap(
                checkListRepository.sumByGoodsAndDateRange(goodsIds, fromDateTime, toDateTime)
        );
        Map<Long, GoodsMovementAggregate> arrivals = goodsIds.isEmpty() ? Map.of() : toMap(
                arrivalListRepository.sumByGoodsAndDateRange(goodsIds, fromDateTime, toDateTime)
        );
        Map<Long, GoodsMovementAggregate> writeoffs = goodsIds.isEmpty() ? Map.of() : toMap(
                writeoffListRepository.sumByGoodsAndDateRange(goodsIds, fromDateTime, toDateTime)
        );

        return goodsPage.map(g -> {
            GoodsMovementAggregate s = sales.get(g.goodsId());
            GoodsMovementAggregate a = arrivals.get(g.goodsId());
            GoodsMovementAggregate w = writeoffs.get(g.goodsId());

            return new GeneralReportRow(
                    g.goodsId(),
                    g.name(),
                    g.residue(),
                    qty(s), qty(a), qty(w),
                    sum(s), sum(a), sum(w),
                    profit(s)
            );
        });
    }

    private Map<Long, GoodsMovementAggregate> toMap(List<GoodsMovementAggregate> list) {
        Map<Long, GoodsMovementAggregate> map = new HashMap<>();
        for (GoodsMovementAggregate a : list) {
            map.put(a.goodsId(), a);
        }
        return map;
    }

    private BigDecimal qty(GoodsMovementAggregate a) {
        return a != null && a.quantity() != null ? a.quantity() : ZERO;
    }

    private BigDecimal sum(GoodsMovementAggregate a) {
        return a != null && a.sum() != null ? a.sum() : ZERO;
    }

    private BigDecimal profit(GoodsMovementAggregate a) {
        return a != null && a.profit() != null ? a.profit() : ZERO;
    }
}
