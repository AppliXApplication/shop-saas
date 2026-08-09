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
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
     * Движение товара за период: остаток (текущий) + продано/оприходовано/списано
     * (кол-во) за диапазон дат, фильтр по категории. Показываются только товары,
     * у которых было хоть какое-то движение — иначе список был бы забит нулями.
     * Суммы (продаж/прихода/списания/прибыли) — общие по всей выборке, отдаются
     * отдельно от постраничного списка строк.
     * GET /api/reports/general?from=2026-08-01&to=2026-08-31&categoryId=3&page=0&size=50
     */
    @GetMapping("/api/reports/general")
    public GeneralReportResponse report(
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

        GeneralReportTotals totals = new GeneralReportTotals(
                checkListRepository.sumSalesValue(fromDateTime, toDateTime, categoryId),
                arrivalListRepository.sumArrivalValue(fromDateTime, toDateTime, categoryId),
                writeoffListRepository.sumWriteoffValue(fromDateTime, toDateTime, categoryId),
                checkListRepository.sumProfit(fromDateTime, toDateTime, categoryId)
        );

        // Объединяем id товаров, у которых было хоть какое-то движение (продажа,
        // приход или списание) за период — только они попадут в таблицу.
        Set<Long> movedIds = new LinkedHashSet<>();
        movedIds.addAll(checkListRepository.findDistinctGoodsIds(fromDateTime, toDateTime, categoryId));
        movedIds.addAll(arrivalListRepository.findDistinctGoodsIds(fromDateTime, toDateTime, categoryId));
        movedIds.addAll(writeoffListRepository.findDistinctGoodsIds(fromDateTime, toDateTime, categoryId));

        int safeSize = Math.min(size, MAX_PAGE_SIZE);
        Pageable pageable = PageRequest.of(page, safeSize);

        if (movedIds.isEmpty()) {
            return new GeneralReportResponse(Page.empty(pageable), totals);
        }

        Page<GoodsResidueRow> goodsPage = goodsRepository.findResidueReportByIds(
                List.copyOf(movedIds), categoryId, pageable
        );

        List<Long> pageIds = goodsPage.getContent().stream().map(GoodsResidueRow::goodsId).toList();

        Map<Long, GoodsMovementAggregate> sales = toMap(
                checkListRepository.sumByGoodsAndDateRange(pageIds, fromDateTime, toDateTime)
        );
        Map<Long, GoodsMovementAggregate> arrivals = toMap(
                arrivalListRepository.sumByGoodsAndDateRange(pageIds, fromDateTime, toDateTime)
        );
        Map<Long, GoodsMovementAggregate> writeoffs = toMap(
                writeoffListRepository.sumByGoodsAndDateRange(pageIds, fromDateTime, toDateTime)
        );

        Page<GeneralReportRow> rows = goodsPage.map(g -> new GeneralReportRow(
                g.goodsId(),
                g.name(),
                g.residue(),
                qty(sales.get(g.goodsId())),
                qty(arrivals.get(g.goodsId())),
                qty(writeoffs.get(g.goodsId()))
        ));

        return new GeneralReportResponse(rows, totals);
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
}
