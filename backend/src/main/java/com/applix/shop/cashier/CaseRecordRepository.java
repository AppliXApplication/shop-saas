package com.applix.shop.cashier;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CaseRecordRepository extends JpaRepository<CaseRecord, Long> {

    @Query("""
            select new com.applix.shop.cashier.CaseRecordRow(
                cr.id, cr.date, u.login,
                coalesce(ci.note, co.note, a.note, w.note),
                ci.sumCash, co.sumCash, a.sumInvoice, w.sum
            )
            from CaseRecord cr
            left join cr.user u
            left join cr.cashIn ci
            left join cr.cashOut co
            left join cr.arrival a
            left join cr.writeoff w
            where cr.date >= :from and cr.date < :to
            order by cr.date asc
            """)
    List<CaseRecordRow> findByDateRange(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("""
            select new com.applix.shop.cashier.CaseRecordDailyTotals(
                coalesce(sum(ci.sumCash), 0),
                coalesce(sum(co.sumCash), 0),
                coalesce(sum(a.sumInvoice), 0),
                coalesce(sum(w.sum), 0),
                count(cr.id)
            )
            from CaseRecord cr
            left join cr.cashIn ci
            left join cr.cashOut co
            left join cr.arrival a
            left join cr.writeoff w
            where cr.date >= :from and cr.date < :to
            """)
    CaseRecordDailyTotals sumByDateRange(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}
