package com.applix.shop.cashier;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface CheckListRepository extends JpaRepository<CheckList, Long> {

    @Query("""
            select new com.applix.shop.cashier.GoodsMovementAggregate(
                cl.goodsId, sum(cl.amount), sum(cl.amount * cl.price), sum(cl.profit)
            )
            from CheckList cl
            where cl.goodsId in :goodsIds
              and cl.check.date >= :from and cl.check.date < :to
            group by cl.goodsId
            """)
    List<GoodsMovementAggregate> sumByGoodsAndDateRange(
            @Param("goodsIds") List<Long> goodsIds,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    @Query("""
            select distinct cl.goodsId
            from CheckList cl
            where cl.check.date >= :from and cl.check.date < :to
              and (:categoryId is null or cl.goods.categoryId = :categoryId)
            """)
    List<Long> findDistinctGoodsIds(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("categoryId") Integer categoryId
    );

    @Query("""
            select coalesce(sum(cl.amount * cl.price), 0)
            from CheckList cl
            where cl.check.date >= :from and cl.check.date < :to
              and (:categoryId is null or cl.goods.categoryId = :categoryId)
            """)
    BigDecimal sumSalesValue(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("categoryId") Integer categoryId
    );

    @Query("""
            select coalesce(sum(cl.profit), 0)
            from CheckList cl
            where cl.check.date >= :from and cl.check.date < :to
              and (:categoryId is null or cl.goods.categoryId = :categoryId)
            """)
    BigDecimal sumProfit(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("categoryId") Integer categoryId
    );
}
