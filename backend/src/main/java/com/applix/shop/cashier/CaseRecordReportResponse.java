package com.applix.shop.cashier;

import java.time.LocalDate;
import java.util.List;

public record CaseRecordReportResponse(
        LocalDate date,
        List<CaseRecordRow> rows,
        CaseRecordDailyTotals totals,
        DailySalesSummary salesSummary
) {
}
