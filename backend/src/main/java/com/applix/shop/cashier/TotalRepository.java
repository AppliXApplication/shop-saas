package com.applix.shop.cashier;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface TotalRepository extends JpaRepository<Total, Integer> {

    /**
     * За день может быть несколько записей (например, по одной на кассира) —
     * поэтому суммируем, а не берём одну строку.
     */
    @Query("""
            select new com.applix.shop.cashier.DailySalesSummary(
                coalesce(sum(t.proceeds), 0),
                coalesce(sum(t.sumGoods), 0),
                coalesce(sum(t.cash), 0),
                coalesce(sum(t.cashless), 0),
                coalesce(sum(t.spare), 0)
            )
            from Total t
            where t.date >= :from and t.date < :to
            """)
    DailySalesSummary sumByDateRange(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}
