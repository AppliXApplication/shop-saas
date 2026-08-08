package com.applix.shop.cashier;

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

    /**
     * Дневной отчёт по кассовым сменам. Без параметра date — сегодняшний день.
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

        return new CaseRecordReportResponse(targetDate, rows, totals);
    }
}
