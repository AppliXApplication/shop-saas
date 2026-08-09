package com.applix.shop.general;

import org.springframework.data.domain.Page;

public record GeneralReportResponse(
        Page<GeneralReportRow> rows,
        GeneralReportTotals totals
) {
}
