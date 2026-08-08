package com.applix.shop.cashier;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface CheckRepository extends JpaRepository<Check, Integer> {

    @Query("select coalesce(sum(c.sum), 0) from Check c where c.date >= :from and c.date < :to")
    BigDecimal sumRevenue(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("select coalesce(sum(c.cash), 0) from Check c where c.date >= :from and c.date < :to")
    BigDecimal sumCash(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("select coalesce(sum(c.cashless), 0) from Check c where c.date >= :from and c.date < :to")
    BigDecimal sumCashless(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    // Отдельный запрос от Check — если бы считали вместе в одном join, суммы
    // чеков задвоились бы на количество строк checklist (fan-out при 1-ко-многим).
    @Query("""
            select coalesce(sum(cl.amount * cl.price - cl.profit), 0)
            from CheckList cl
            where cl.check.date >= :from and cl.check.date < :to
            """)
    BigDecimal sumGoodsValue(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}
