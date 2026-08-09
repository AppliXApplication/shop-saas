package com.applix.shop.cashier;

import com.applix.shop.goods.GoodsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CaseRecordReportController {

    private final CaseRecordRepository caseRecordRepository;
    private final CheckRepository checkRepository;
    private final GoodsRepository goodsRepository;
    private final TotalRepository totalRepository;

    /**
     * Дневной отчёт по кассовым сменам + сводка по продажам. Без параметра date — сегодня.
     * GET /api/reports/case-record?date=2026-08-02
     */
    @GetMapping("/api/reports/case-record")
    public CaseRecordReportResponse report(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        LocalDate targetDate = date != null ? date : LocalDate.now();
        LocalDateTime from = targetDate.atStartOfDay();
        LocalDateTime to = from.plusDays(1);

        List<CaseRecordRow> rows = caseRecordRepository.findByDateRange(from, to);
        CaseRecordDailyTotals totals = caseRecordRepository.sumByDateRange(from, to);

        DailySalesSummary salesSummary = targetDate.isBefore(LocalDate.now())
                ? totalRepository.sumByDateRange(from, to)
                : buildLiveSalesSummary(from, to, totals);

        return new CaseRecordReportResponse(targetDate, rows, totals, salesSummary);
    }

    /**
     * Для сегодняшнего (текущего) дня в total ещё нет готового снепшота —
     * считаем вживую из check/cash_in/cash_out/goods.
     */
    private DailySalesSummary buildLiveSalesSummary(
            LocalDateTime from, LocalDateTime to, CaseRecordDailyTotals totals
    ) {
        // Наличные = нал по чекам + внесение (cash_in) - изъятие (cash_out) за день.
        BigDecimal cashSum = checkRepository.sumCash(from, to)
                .add(totals.cashInTotal())
                .subtract(totals.cashOutTotal());

        return new DailySalesSummary(
                checkRepository.sumRevenue(from, to),
                // Текущая стоимость остатков (residue*price по всем товарам) —
                // тот же расчёт, что и на странице "Остатки товаров".
                goodsRepository.sumResidueValue(null),
                cashSum,
                checkRepository.sumCashless(from, to)
        );
    }
}



