package com.applix.shop.cashier;

import com.applix.shop.goods.GoodsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CaseRecordReportController {

    private final CaseRecordRepository caseRecordRepository;
    private final CheckRepository checkRepository;
    private final GoodsRepository goodsRepository;

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

        DailySalesSummary salesSummary = new DailySalesSummary(
                checkRepository.sumRevenue(from, to),
                // "Сумма в товаре" — текущая стоимость остатков (residue*price по всем
                // товарам), тот же расчёт, что и на странице "Остатки товаров". Это
                // снимок на текущий момент, а не исторический показатель за дату отчёта —
                // в goods нет привязки остатка к дате.
                goodsRepository.sumResidueValue(null),
                checkRepository.sumCash(from, to),
                checkRepository.sumCashless(from, to)
        );

        return new CaseRecordReportResponse(targetDate, rows, totals, salesSummary);
    }
}


